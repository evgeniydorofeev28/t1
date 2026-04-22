package com.novisign.collector.service;

import org.springframework.stereotype.Service;

import com.novisign.collector.dao.StatPlayerInfoDAO;
import com.novisign.collector.model.StatPlayerInfo;
import com.novisign.collector.parser.StatPlayerInfoParser;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class StatPlayerInfoService {
  private final StatPlayerInfoDAO statPlayerInfoRepository;

  @DatabaseRetry
  public void create(String csv, String externalAddress) {
    StatPlayerInfo spi = StatPlayerInfoParser.parseInsert(csv, externalAddress);
    log.debug("Creating new StatPlayerInfo {}", spi.getPlayerId());
    if (!statPlayerInfoRepository.create(spi)) {
      log.warn("StatPlayerInfo {} already exists", spi.getPlayerId());
    }
  }

  @DatabaseRetry
  public void update(String csv, String externalAddress) {
    StatPlayerInfo spi = StatPlayerInfoParser.parseUpdate(csv);
    spi.setExternalAddress(externalAddress);
    log.debug("Updating StatPlayerInfo {}", spi.getPlayerId());
    if (!statPlayerInfoRepository.update(spi)) {
      log.warn("StatPlayerInfo {} does not exist", spi.getPlayerId());
    }
  }

  @DatabaseRetry
  public void updateStatus(String playerId, String status, String externalAddress) {
    log.debug("Updating StatPlayerInfo {} status", playerId);
    if (!statPlayerInfoRepository.updateStatus(playerId, externalAddress, status)) {
      log.warn("StatPlayerInfo {} does not exist", playerId);
    }
  }
}
