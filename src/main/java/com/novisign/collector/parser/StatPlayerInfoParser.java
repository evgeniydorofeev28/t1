package com.novisign.collector.parser;

import com.novisign.collector.exception.BadRequestException;
import com.novisign.collector.model.StatPlayerInfo;

public class StatPlayerInfoParser {

  public static StatPlayerInfo parseInsert(String csv, String externalAddress) {
    try {
      StatPlayerInfo spi = new StatPlayerInfo();
      String[] a = ParserUtil.split(csv);
      int version = ParserUtil.getVersion(a, 4);
      int i = 2;
      spi.setPlayerId(a[i++]);
      spi.setPlayerName(a[i++]);
      spi.setScreenKey(a[i++]);
      spi.setInternalAddress(a[i++]);
      if (version > 1) {
        spi.setPlayerState(ParserUtil.decodeBase64(a[i++]));
      }
      spi.setDeviceName(a[i++]);
      spi.setOsName(a[i++]);
      spi.setOsVersion(a[i++]);
      spi.setPlayerVersion(a[i++]);
      spi.setPlayerBrand(a[i++]);
      spi.setPlayerType(a[i++]);
      if (version > 2) {
        spi.setCustomExtraInfo(a[i++]);
      }
      spi.setExternalAddress(externalAddress);
      if (version > 3) {
        spi.setInstallId(a[i++]);
        spi.setInstallSource(a[i++]);
        spi.setAutoUpgrade(ParserUtil.parseBoolean(a[i++]));
        spi.setAdditInfo(a[i++]);
      }
      spi.setLastUpdateTime(System.currentTimeMillis());
      return spi;
    } catch (Exception e) {
      throw new BadRequestException("Failed to parse " + csv + ": " + e.getMessage());
    }
  }

  public static StatPlayerInfo parseUpdate(String csv) {
    try {
      String[] a = ParserUtil.split(csv);
      int version = ParserUtil.getVersion(a, 3);
      int i = 2;
      StatPlayerInfo spi = new StatPlayerInfo();
      spi.setPlayerId(a[i++]);
      spi.setPlayerName(a[i++]);
      spi.setScreenKey(a[i++]);
      spi.setInternalAddress(a[i++]);
      if (version > 1) {
        spi.setPlayerState(ParserUtil.decodeBase64(a[i++]));
      }
      if (version > 2) {
        spi.setInstallId(a[i++]);
        spi.setInstallSource(a[i++]);
        spi.setAutoUpgrade(ParserUtil.parseBoolean(a[i++]));
        spi.setAdditInfo(a[i++]);
      }
      spi.setLastUpdateTime(System.currentTimeMillis());
      return spi;
    } catch (Exception e) {
      throw new BadRequestException("Failed to parse " + csv + ": " + e.getMessage());
    }
  }
}