package com.novisign.collector.parser;

import lombok.Data;

@Data
public class StatReportHeader {
  private String reporterId;
  private Long reporterTime;
  private String reporterTimezone;
  private int reporterTimezoneOffset;
}
