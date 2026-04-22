package com.novisign.collector.parser;

import java.util.List;
import java.util.TimeZone;

import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriComponentsBuilder;

import com.novisign.collector.model.ClicksGlobalCount;
import com.novisign.collector.model.StatEvent;
import com.novisign.collector.model.StatMedia;

public class MediaReportParser {
  private static final String AGGREGATE_CLICKS_NAME_FLAG = "aggregateClicksName";

  public static StatReportHeader parseHeader(String csv) {
    try {
      StatReportHeader h = new StatReportHeader();
      String[] a = ParserUtil.split(csv);
      ParserUtil.getVersion(a, 1);
      h.setReporterId(a[2]);
      h.setReporterTime(ParserUtil.parseLong(a[3]));
      h.setReporterTimezone(a[4]);
      TimeZone tz = TimeZone.getTimeZone(a[4]);
      h.setReporterTimezoneOffset(tz.getRawOffset() + tz.getDSTSavings());
      return h;
    } catch (Exception e) {
      throw new RuntimeException("Failed to parse " + csv + ": " + e.getMessage());
    }
  }

  public static StatMedia parseMedia(String csv, String filename, StatReportHeader header) {
    try {
      StatMedia m = new StatMedia();
      String[] a = ParserUtil.split(csv);
      int version = ParserUtil.getVersion(a, 3);
      int i = 2;
      m.setReportId(filename);
      m.setPlayerId(header.getReporterId());
      m.setTimezoneId(header.getReporterTimezone());
      m.setTimezoneOffset(header.getReporterTimezoneOffset());
      m.setClientReportTime(header.getReporterTime());
      m.setServerReportTime(Long.parseLong(filename.split("_")[0]));
      m.setEntryReportTime(Long.parseLong(a[i++]));
      m.setScreenKey(a[i++]);
      m.setMainPlaylistKey(a[i++]);
      m.setPlaylistKey(a[i++]);
      m.setCreativeKey(a[i++]);
      m.setMediaKey(a[i++]);
      if (version > 1) {
        m.setWidgetName(a[i++]);
      } else {
        m.setWidgetName("");
      }
      m.setWidth(ParserUtil.parseInt(a[i++]));
      m.setHeight(ParserUtil.parseInt(a[i++]));
      if (version > 2) {
        m.setFlags(a[i++]);
      }
      m.setPlayDuration(ParserUtil.parseLong(a[i++]));
      m.setPlayCount(ParserUtil.parseLong(a[i++]));
      if (version > 1) {
        m.setClickCount(ParserUtil.parseInt(a[i++]));
      }
      m.setEntryReportLocalTime(m.getEntryReportTime() + m.getTimezoneOffset());
      return m;
    } catch (Exception e) {
      throw new RuntimeException("Failed to parse " + csv + ": " + e.getMessage());
    }
  }

  public static StatEvent parseEvent(String csv, String filename) {
    try {
      StatEvent e = new StatEvent();
      String[] a = ParserUtil.split(csv);
      ParserUtil.getVersion(a, 1);
      int i = 2;
      e.setReportId(filename);
      e.setReportTime(ParserUtil.parseLong(a[i++]));
      e.setScreenKey(a[i++]);
      e.setEventType(a[i++]);
      e.setCount(ParserUtil.parseLong(a[i++]));
      e.setEventKey("-");
      return e;
    } catch (Exception e) {
      throw new RuntimeException("Failed to parse " + csv + ": " + e.getMessage());
    }
  }

  public static ClicksGlobalCount parseClicksGlobalCount(StatMedia statMedia) {
    String aggregateName = getAggregateClicksName(statMedia.getFlags());
    if (aggregateName == null || statMedia.getClickCount() == 0) {
      return null;
    }
    ClicksGlobalCount cc = new ClicksGlobalCount();
    cc.setScreenKey(statMedia.getScreenKey());
    cc.setCount(statMedia.getClickCount());
    cc.setAggregateName(aggregateName);
    cc.setModified(System.currentTimeMillis());
    return cc;
  }

  private static String getAggregateClicksName(String flags) {
    if (flags == null || flags.isEmpty()) {
      return null;
    }
    MultiValueMap<String, String> flagsList =  UriComponentsBuilder.newInstance().
        query(flags).build().getQueryParams();
    List<String> list = flagsList.get(AGGREGATE_CLICKS_NAME_FLAG);
    if (list == null || list.isEmpty()) {
      return null;
    }
    return list.get(0);
  }
}