package com.novisign.collector.service;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.zip.GZIPInputStream;

import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import com.novisign.collector.dao.MediaReportDAO;
import com.novisign.collector.exception.BadRequestException;
import com.novisign.collector.model.ClicksGlobalCount;
import com.novisign.collector.model.StatEvent;
import com.novisign.collector.model.StatMedia;
import com.novisign.collector.parser.StatReportHeader;
import com.novisign.collector.parser.MediaReportParser;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class MediaReportService {
  private final LineProcessor reportLineProcessor;

  public void processMediaReport(String reportId, byte[] content) throws IOException {
    try (InputStream is = new ByteArrayInputStream(content);
         GZIPInputStream gzis = new GZIPInputStream(is);
         InputStreamReader isr = new InputStreamReader(gzis);
         BufferedReader br = new BufferedReader(isr)) {
      String line = br.readLine();
      StatReportHeader header = MediaReportParser.parseHeader(line);
      while ((line = br.readLine()) != null) {
        try {
          reportLineProcessor.processLine(line, reportId, header);
        } catch (Exception e) {
          log.error("Failed to process line {} in {}", line, reportId, e);
        }
      }
    }
  }

  @Component
  @RequiredArgsConstructor
  @Slf4j
  static class LineProcessor {
    private final MediaReportDAO repository;

    @DatabaseRetry
    void processLine(String line, String filename, StatReportHeader header) {
      if (line.startsWith("m")) {
        StatMedia m = MediaReportParser.parseMedia(line, filename, header);
        repository.saveMedia(m);
        ClicksGlobalCount cc = MediaReportParser.parseClicksGlobalCount(m);
        if (cc != null) {
          repository.updateClicksGlobalCount(cc);
        }
      } else if (line.startsWith("e")) {
        StatEvent e = MediaReportParser.parseEvent(line, filename);
        repository.saveEvent(e);
      } else {
        throw new BadRequestException("Unsupported line type: " + line.charAt(0));
      }
    }
  }
}
