package com.novisign.collector.entity;

import java.util.concurrent.TimeUnit;

import lombok.Data;

@Data
public class StatPlayerInfo {
  private String playerId;
  private String playerName;
  private String screenKey;
  private String internalAddress;
  private String playerState;
  private String deviceName;
  private String osName;
  private String osVersion;
  private String playerVersion;
  private String playerBrand;
  private String playerType;
  private String customExtraInfo;
  private String externalAddress;
  private String installId;
  private String installSource;
  private Boolean autoUpgrade;
  private String additInfo;
  private Long lastUpdateTime;
  private Long disableScreenDetectTime;
  private String playerStatus;
  
  public void setLastUpdateTimeAndDisableScreenDetectTime() {
    long now = System.currentTimeMillis();
    if (lastUpdateTime != null && now - lastUpdateTime > TimeUnit.HOURS.toMillis(2)) {
      disableScreenDetectTime = now;
    }
    lastUpdateTime = now;
  }
}
