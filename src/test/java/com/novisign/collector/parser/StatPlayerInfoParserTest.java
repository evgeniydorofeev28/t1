package com.novisign.collector.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import com.novisign.collector.model.StatPlayerInfo;

public class StatPlayerInfoParserTest {

  @Test
  public void parseSpiV1i() {
    StatPlayerInfo spi = StatPlayerInfoParser.parseInsert("i;1;playerId;playerName;screenKey;internalAddress;deviceName;osName;osVersion;playerVersion;playerBrand;playerType;\n", "externalAddress");

    assertEquals("playerId", spi.getPlayerId());
    assertEquals("playerName", spi.getPlayerName());
    assertEquals("screenKey", spi.getScreenKey());
    assertEquals("internalAddress", spi.getInternalAddress());
    assertEquals("deviceName", spi.getDeviceName());
    assertEquals("osName", spi.getOsName());
    assertEquals("osVersion", spi.getOsVersion());
    assertEquals("playerVersion", spi.getPlayerVersion());
    assertEquals("playerBrand", spi.getPlayerBrand());
    assertEquals("playerType", spi.getPlayerType());
    assertEquals("externalAddress", spi.getExternalAddress());
  }

  @Test
  public void parseSpiV2i() {
    StatPlayerInfo spi = StatPlayerInfoParser.parseInsert("i;2;playerId;playerName;screenKey;internalAddress;c29tZXRoaW5nQmFzZTY0OQ==;deviceName;osName;osVersion;playerVersion;playerBrand;playerType;\n", "externalAddress");

    assertEquals("playerId", spi.getPlayerId());
    assertEquals("playerName", spi.getPlayerName());
    assertEquals("screenKey", spi.getScreenKey());
    assertEquals("internalAddress", spi.getInternalAddress());
    assertEquals("deviceName", spi.getDeviceName());
    assertEquals("osName", spi.getOsName());
    assertEquals("osVersion", spi.getOsVersion());
    assertEquals("playerVersion", spi.getPlayerVersion());
    assertEquals("playerBrand", spi.getPlayerBrand());
    assertEquals("playerType", spi.getPlayerType());
    assertEquals("externalAddress", spi.getExternalAddress());
    assertEquals("somethingBase649", spi.getPlayerState());
  }

  @Test
  public void parseSpiV3i() {
    StatPlayerInfo spi = StatPlayerInfoParser.parseInsert("i;3;playerId;playerName;screenKey;internalAddress;c29tZXRoaW5nQmFzZTY0OQ==;deviceName;osName;osVersion;playerVersion;playerBrand;playerType;customExtraInfo\n", "externalAddress");

    assertEquals("playerId", spi.getPlayerId());
    assertEquals("playerName", spi.getPlayerName());
    assertEquals("screenKey", spi.getScreenKey());
    assertEquals("internalAddress", spi.getInternalAddress());
    assertEquals("deviceName", spi.getDeviceName());
    assertEquals("osName", spi.getOsName());
    assertEquals("osVersion", spi.getOsVersion());
    assertEquals("playerVersion", spi.getPlayerVersion());
    assertEquals("playerBrand", spi.getPlayerBrand());
    assertEquals("playerType", spi.getPlayerType());
    assertEquals("externalAddress", spi.getExternalAddress());
    assertEquals("somethingBase649", spi.getPlayerState());
    assertEquals("customExtraInfo", spi.getCustomExtraInfo());
  }

  @Test
  public void parseSpiV4i() {
    StatPlayerInfo spi = StatPlayerInfoParser.parseInsert("i;4;playerId;playerName;screenKey;internalAddress;c29tZXRoaW5nQmFzZTY0OQ==;deviceName;osName;osVersion;playerVersion;playerBrand;playerType;customExtraInfo;installId;installSource;1;additInfo\n", "externalAddress");

    assertEquals("playerId", spi.getPlayerId());
    assertEquals("playerName", spi.getPlayerName());
    assertEquals("screenKey", spi.getScreenKey());
    assertEquals("internalAddress", spi.getInternalAddress());
    assertEquals("deviceName", spi.getDeviceName());
    assertEquals("osName", spi.getOsName());
    assertEquals("osVersion", spi.getOsVersion());
    assertEquals("playerVersion", spi.getPlayerVersion());
    assertEquals("playerBrand", spi.getPlayerBrand());
    assertEquals("playerType", spi.getPlayerType());
    assertEquals("externalAddress", spi.getExternalAddress());
    assertEquals("somethingBase649", spi.getPlayerState());
    assertEquals("customExtraInfo", spi.getCustomExtraInfo());
    assertEquals("installId", spi.getInstallId());
    assertEquals("installSource", spi.getInstallSource());
    assertEquals(true, spi.getAutoUpgrade());
    assertEquals("additInfo", spi.getAdditInfo());
  }

  @Test
  public void parseSpiV1u() {
    StatPlayerInfo spi = StatPlayerInfoParser.parseUpdate("u;1;playerId;playerName;screenKey;internalAddress;\n");

    assertEquals("playerId", spi.getPlayerId());
    assertEquals("playerName", spi.getPlayerName());
    assertEquals("screenKey", spi.getScreenKey());
    assertEquals("internalAddress", spi.getInternalAddress());
  }

  @Test
  public void parseSpiV2u() {
    StatPlayerInfo spi = StatPlayerInfoParser.parseUpdate("u;2;playerId;playerName;screenKey;internalAddress;c29tZXRoaW5nQmFzZTY0OQ==\n");

    assertEquals("playerId", spi.getPlayerId());
    assertEquals("playerName", spi.getPlayerName());
    assertEquals("screenKey", spi.getScreenKey());
    assertEquals("internalAddress", spi.getInternalAddress());
    assertEquals("somethingBase649", spi.getPlayerState());
  }

  @Test
  public void parseSpiV3u() {
    StatPlayerInfo spi = StatPlayerInfoParser.parseUpdate("u;3;playerId;playerName;screenKey;internalAddress;c29tZXRoaW5nQmFzZTY0OQ==;installId;installSource;1;additInfo\n");

    assertEquals("playerId", spi.getPlayerId());
    assertEquals("playerName", spi.getPlayerName());
    assertEquals("screenKey", spi.getScreenKey());
    assertEquals("internalAddress", spi.getInternalAddress());
    assertEquals("somethingBase649", spi.getPlayerState());
    assertEquals("installId", spi.getInstallId());
    assertEquals("installSource", spi.getInstallSource());
    assertEquals(true, spi.getAutoUpgrade());
    assertEquals("additInfo", spi.getAdditInfo());
  }

}
