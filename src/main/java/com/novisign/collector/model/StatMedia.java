package com.novisign.collector.model;

import lombok.Data;

@Data
public class StatMedia {
  private String reportId;
  private String screenKey;
  private String playerId;
  private String mainPlaylistKey;
  private String playlistKey;
  private String creativeKey;
  private String mediaKey;
  private String widgetName;
  private String timezoneId;
  private int timezoneOffset;
  private long playDuration;
  private long playCount;
  private long clickCount;
  private int width;
  private int height;
  private long entryReportTime;
  private long entryReportLocalTime;
  private long clientReportTime;
  private long serverReportTime;
  private String flags;
}
