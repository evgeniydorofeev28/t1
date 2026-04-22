package com.novisign.collector.model;

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
  private String playerStatus;
  private Long lastUpdateTime;
  private Long disableScreenDetectTime;
}
