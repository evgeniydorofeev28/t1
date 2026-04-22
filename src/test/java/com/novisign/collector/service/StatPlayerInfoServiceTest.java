package com.novisign.collector.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.jdbc.Sql;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.novisign.collector.configuration.Configuration;
import com.novisign.collector.configuration.Configuration.QueueUrl;
import com.novisign.collector.dao.StatPlayerInfoDAO;
import com.novisign.collector.model.StatPlayerInfo;

import lombok.Data;

@JdbcTest
@Testcontainers
@Import({StatPlayerInfoService.class, StatPlayerInfoDAO.class})
@Sql("/init-db.sql")
public class StatPlayerInfoServiceTest {
  private static final String INSERT_CSV = "i;4;playerId;playerName;screenKey;internalAddress;c29tZXRoaW5nQmFzZTY0OQ==;deviceName;osName;osVersion;playerVersion;playerBrand;playerType;customExtraInfo;installId;installSource;1;additInfo";
  private static final String UPDATE_CSV = "u;3;playerId;playerName;screenKey;internalAddress;c29tZXRoaW5nQmFzZTY0OQ==;installId;installSource;1;additInfo";

  @Container
  @ServiceConnection
  private static final MySQLContainer<?> mysql = new MySQLContainer<>("mysql:8.0");
  
  @MockitoBean Configuration config;
  @MockitoBean QueueUrl queueUrl;
  
  @Autowired JdbcClient jdbcClient;
  @Autowired StatPlayerInfoService statPlayerInfoService;

  long now = System.currentTimeMillis();

  @Test
  public void create() {
    statPlayerInfoService.create(INSERT_CSV, "externalAddress");

    StatPlayerInfo spi = findOne();
    assertEquals("playerId", spi.getPlayerId());
    assertEquals("playerName", spi.getPlayerName());
    assertEquals("screenKey", spi.getScreenKey());
    assertEquals("internalAddress", spi.getInternalAddress());
    assertEquals("externalAddress", spi.getExternalAddress());
    assertEquals("somethingBase649", spi.getPlayerState());
    assertEquals("deviceName", spi.getDeviceName());
    assertEquals("osName", spi.getOsName());
    assertEquals("osVersion", spi.getOsVersion());
    assertEquals("playerVersion", spi.getPlayerVersion());
    assertEquals("playerBrand", spi.getPlayerBrand());
    assertEquals("playerType", spi.getPlayerType());
    assertEquals("installId", spi.getInstallId());
    assertEquals("installSource", spi.getInstallSource());
    assertEquals(true, spi.getAutoUpgrade());
    assertEquals("additInfo", spi.getAdditInfo());
    assertTrue(spi.getLastUpdateTime() >= now);
    
    checkStatusActivity(null);
  }

  @Test
  public void update() {
    createOne(now - TimeUnit.HOURS.toMillis(1), 1000);

    statPlayerInfoService.update(UPDATE_CSV, "externalAddress");

    StatPlayerInfo spi = findOne();
    assertNotNull(spi);
    assertEquals("playerId", spi.getPlayerId());
    assertEquals("playerName", spi.getPlayerName());
    assertEquals("screenKey", spi.getScreenKey());
    assertEquals("internalAddress", spi.getInternalAddress());
    assertEquals("externalAddress", spi.getExternalAddress());
    assertEquals("somethingBase649", spi.getPlayerState());
    assertEquals("installId", spi.getInstallId());
    assertEquals("installSource", spi.getInstallSource());
    assertEquals(true, spi.getAutoUpgrade());
    assertEquals("additInfo", spi.getAdditInfo());
    assertTrue(spi.getLastUpdateTime() >= now);
    assertEquals(1000, spi.getDisableScreenDetectTime());

    checkStatusActivity(null);
  }
  
  @Test
  public void update_set_disableScreenDetectTime() {
    createOne(now - TimeUnit.HOURS.toMillis(3), 1000);
    
    statPlayerInfoService.update(UPDATE_CSV, "externalAddress");

    StatPlayerInfo spi = findOne();
    assertEquals(spi.getLastUpdateTime(), spi.getDisableScreenDetectTime());
  }

  @Test
  public void updateStatus() {
    createOne(now - TimeUnit.HOURS.toMillis(1), 1000);

    statPlayerInfoService.updateStatus("playerId", "active", "externalAddress");

    StatPlayerInfo spi = findOne();
    assertNotNull(spi);
    assertEquals("playerId", spi.getPlayerId());
    assertEquals("active", spi.getPlayerStatus());
    assertEquals("externalAddress", spi.getExternalAddress());
    assertTrue(spi.getLastUpdateTime() >= now);
    assertEquals(1000, spi.getDisableScreenDetectTime());
    
    checkStatusActivity("active");
  }
  
  @Test
  public void updateStatus_set_disableScreenDetectTime() {
    createOne(now - TimeUnit.HOURS.toMillis(3), 1000);
    
    statPlayerInfoService.updateStatus("playerId", "active", "externalAddress");

    StatPlayerInfo spi = findOne();
    assertEquals(spi.getLastUpdateTime(), spi.getDisableScreenDetectTime());
  }

  
  @Data
  private static class StatActivity {
    private String playerKey;
    private LocalDateTime activityDate;
    private String remoteIpAddr;
    private String playerId;
    private String playerStatus;
  }

  private void checkStatusActivity(String playerStatus) {
    StatActivity sa = jdbcClient.sql("SELECT * FROM stat_activity").query(StatActivity.class).single();
    assertTrue(sa.getActivityDate() != null);
    assertEquals("playerId", sa.getPlayerId());
    assertEquals("screenKey", sa.getPlayerKey());
    assertEquals("externalAddress", sa.getRemoteIpAddr());
    assertEquals(playerStatus, sa.getPlayerStatus());
  }

  private void createOne(long lastUpdateTime, long disableScreenDetectTime) {
    jdbcClient.sql("INSERT INTO stat_player_info (player_id, screen_key, last_update_time, disable_screen_detect_time) VALUES ('playerId', 'screenKey', ?, ?)")
      .param(lastUpdateTime)
      .param(disableScreenDetectTime)
      .update();
  }
  
  private StatPlayerInfo findOne() {
    return jdbcClient.sql("SELECT * FROM stat_player_info").query(StatPlayerInfo.class).single();
  }
}
