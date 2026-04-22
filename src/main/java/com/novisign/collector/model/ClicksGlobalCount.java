package com.novisign.collector.model;

import lombok.Data;

@Data
public class ClicksGlobalCount {
  private String screenKey;
  private String companyKey;
  private String aggregateName;
  private long count;
  private long modified;
}
