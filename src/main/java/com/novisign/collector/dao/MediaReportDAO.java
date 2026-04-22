package com.novisign.collector.dao;

import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import com.novisign.collector.model.ClicksGlobalCount;
import com.novisign.collector.model.StatEvent;
import com.novisign.collector.model.StatMedia;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class MediaReportDAO {
  private final JdbcClient jdbcClient;

  public int saveMedia(StatMedia sm) {
    return jdbcClient.sql("""
      INSERT INTO stat_media (
        report_id,
        player_id,
        screen_key,
        main_playlist_key,
        playlist_key,
        creative_key,
        media_key,
        widget_name,
        timezone_id,
        timezone_offset,
        play_duration,
        play_count,
        click_count,
        height,
        width,
        entry_report_time,
        client_report_time,
        server_report_time,
        entry_report_local_time
      ) VALUES (
        :reportId,
        :playerId,
        :screenKey,
        :mainPlaylistKey,
        :playlistKey,
        :creativeKey,
        :mediaKey,
        :widgetName,
        :timezoneId,
        :timezoneOffset,
        :playDuration,
        :playCount,
        :clickCount,
        :height,
        :width,
        :entryReportTime,
        :clientReportTime,
        :serverReportTime,
        :entryReportLocalTime
      )
      """)
      .paramSource(sm)
      .update();
  }
  
  public int saveEvent(StatEvent se) {
    return jdbcClient.sql("""
      INSERT INTO stat_event (
        report_id,
        screen_key,
        event_key,
        event_type,
        count,
        report_time
      ) VALUES (
        :reportId,
        :screenKey,
        '-',
        :eventType,
        :count,
        :reportTime
      )
      """)
      .paramSource(se)
      .update();
  }
  
  public int updateClicksGlobalCount(ClicksGlobalCount cc) {
    return jdbcClient.sql("""
      INSERT INTO clicks_global_count (company_key, aggregate_name, count, modified)
      SELECT company_key, :aggregateName, :count, :modified FROM screen
      WHERE screen_key = :screenKey
      ON DUPLICATE KEY UPDATE count = count + :count, modified = :modified
      """)
      .param("aggregateName", cc.getAggregateName())
      .param("count", cc.getCount())
      .param("screenKey", cc.getScreenKey())
      .param("modified", cc.getModified())
      .update();
  }
}
