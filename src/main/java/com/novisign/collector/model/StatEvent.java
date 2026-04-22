package com.novisign.collector.model;

import lombok.Data;

@Data
public class StatEvent {
  private String reportId;
  private String screenKey;
  private String eventKey;
  private String eventType;
  private long count;
  private long reportTime;
}
