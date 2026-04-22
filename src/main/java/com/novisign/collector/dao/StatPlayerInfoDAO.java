package com.novisign.collector.dao;

import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import com.novisign.collector.model.StatPlayerInfo;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class StatPlayerInfoDAO {
  private final JdbcClient jdbcClient;

  public boolean create(StatPlayerInfo spi) {
    int n =jdbcClient.sql("""
      INSERT INTO stat_player_info (
        player_id,
        player_name,
        screen_key,
        internal_address,
        external_address,
        player_state,
        device_name,
        os_name,
        os_version,
        player_version,
        player_brand,
        player_type,
        custom_extra_info,
        install_id,
        install_source,
        auto_upgrade,
        addit_info,
        last_update_time
      ) VALUES (
        :playerId,
        :playerName,
        :screenKey,
        :internalAddress,
        :externalAddress,
        :playerState,
        :deviceName,
        :osName,
        :osVersion,
        :playerVersion,
        :playerBrand,
        :playerType,
        :customExtraInfo,
        :installId,
        :installSource,
        :autoUpgrade,
        :additInfo,
        UNIX_TIMESTAMP() * 1000
      ) ON DUPLICATE KEY UPDATE player_id = player_id
      """)
      .paramSource(spi)
      .update();
    
    if (n != 1) {
      return false;
    }
    createStatActivity(spi.getPlayerId());
    return true;
  }
  
  public boolean update(StatPlayerInfo spi) {
    int n = jdbcClient.sql("""
      UPDATE stat_player_info t1
      JOIN stat_player_info t2 ON t1.player_id = t2.player_id
      SET
        t1.player_name = :playerName,
        t1.screen_key = :screenKey,
        t1.internal_address = :internalAddress,
        t1.external_address = :externalAddress,
        t1.player_state = :playerState,
        t1.disable_screen_detect_time = CASE WHEN UNIX_TIMESTAMP() - t2.last_update_time / 1000 > 3600 * 2 THEN :lastUpdateTime ELSE t2.disable_screen_detect_time END,
        t1.last_update_time = :lastUpdateTime,
        t1.install_id = :installId,
        t1.install_source = :installSource,
        t1.auto_upgrade = :autoUpgrade,
        t1.addit_info = :additInfo
      WHERE t1.player_id = :playerId
      """)
      .paramSource(spi)
      .update();
    
    if (n == 0) {
      return false;
    }
    createStatActivity(spi.getPlayerId());
    return true;
  }

  public boolean updateStatus(String playerId, String status, String externalAddress) {
    int n = jdbcClient.sql("""
      UPDATE stat_player_info t1
      JOIN stat_player_info t2 ON t1.player_id = t2.player_id
      SET
        t1.external_address = :externalAddress,
        t1.player_status = :playerStatus,
        t1.last_update_time = :currentTimeMillis,
        t1.disable_screen_detect_time = CASE WHEN UNIX_TIMESTAMP() - t2.last_update_time / 1000 > 3600 * 2 THEN :currentTimeMillis ELSE t2.disable_screen_detect_time END
      WHERE t1.player_id = :playerId
      """)
      .param("playerId", playerId)
      .param("externalAddress", externalAddress)
      .param("playerStatus", status)
      .param("currentTimeMillis", System.currentTimeMillis())
      .update();
    
    if (n == 0) {
      return false;
    }
    createStatActivity(playerId);
    return true;
  }
  
  private void createStatActivity(String playerId) {
    jdbcClient.sql("""
      INSERT INTO stat_activity (
        player_key,
        activity_date,
        remote_ip_addr,
        player_id,
        player_status
      ) SELECT
        screen_key, 
        NOW(),
        external_address,
        player_id,
        player_status
      FROM stat_player_info
      WHERE player_id = ?
       """) 
      .param(playerId)
      .update();
  }

}


