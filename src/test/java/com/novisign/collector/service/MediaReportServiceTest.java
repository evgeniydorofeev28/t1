package com.novisign.collector.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.GZIPOutputStream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.jdbc.Sql;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.novisign.collector.configuration.Configuration;
import com.novisign.collector.configuration.Configuration.QueueUrl;
import com.novisign.collector.dao.MediaReportDAO;
import com.novisign.collector.model.ClicksGlobalCount;
import com.novisign.collector.model.StatEvent;
import com.novisign.collector.model.StatMedia;
import com.novisign.collector.service.MediaReportService.LineProcessor;

@JdbcTest
@Testcontainers
@Import({MediaReportDAO.class, LineProcessor.class, MediaReportService.class})
@Sql("/init-db.sql")
public class MediaReportServiceTest {
  private static final String REPORT_ID = "1667025606646_a_b23de1ed2b00b1f14741849adb25f889_82117697409995960.mrps";
  private static final String REPORT_CONTENT = """
      h;1;a_b23de1ed2b00b1f14741849adb25f889;1667029265129;GMT;
      e;1;1667026806983;00016e7b-8e8a-455a-86b0-b83f73edee43;type;1;
      m;3;1667026806983;00016e7b-8e8a-455a-86b0-b83f73edee43;3c4223c7-7033-45c9-9bde-d0ea7c088f34;-;e17cc281-1989-4922-8f41-cecc4ca92633.cnt;-;widget;1;2;aggregateClicksName=x;19802318;3;4;
      """;
  
  @Container
  @ServiceConnection
  private static final MySQLContainer<?> mysql = new MySQLContainer<>("mysql:8.0");
  
  @MockitoBean Configuration config;
  @MockitoBean QueueUrl queueUrl;
  
  @Autowired JdbcClient jdbcClient;
  @Autowired MediaReportService service;
  
  private byte[] gzippedReport;
  
  @BeforeEach
  void setUp() throws IOException {
    jdbcClient.sql("INSERT INTO screen (screen_key, company_key) VALUES ('00016e7b-8e8a-455a-86b0-b83f73edee43', '00016e7b-8e8a-455a-86b0-b83f73edee44')").update();
    
    ByteArrayOutputStream bos = new ByteArrayOutputStream();
    try (GZIPOutputStream gzipOS = new GZIPOutputStream(bos)) {
      gzipOS.write(REPORT_CONTENT.getBytes());
    }
    gzippedReport = bos.toByteArray();
  }
  
  @Test
  public void processReport() throws IOException {
    service.processMediaReport(REPORT_ID, gzippedReport);
    
    StatMedia sm = jdbcClient.sql("SELECT * FROM stat_media").query(StatMedia.class).single();
    assertEquals(REPORT_ID, sm.getReportId());
    assertEquals(1667026806983L, sm.getEntryReportTime());
    assertEquals("00016e7b-8e8a-455a-86b0-b83f73edee43", sm.getScreenKey());
    assertEquals("3c4223c7-7033-45c9-9bde-d0ea7c088f34", sm.getMainPlaylistKey());
    assertEquals("-", sm.getPlaylistKey());
    assertEquals("e17cc281-1989-4922-8f41-cecc4ca92633.cnt", sm.getCreativeKey());
    assertEquals("-", sm.getMediaKey());
    assertEquals("widget", sm.getWidgetName());
    assertEquals("GMT", sm.getTimezoneId());
    assertEquals(0, sm.getTimezoneOffset());
    assertEquals(19802318, sm.getPlayDuration());
    assertEquals(1, sm.getWidth());
    assertEquals(2, sm.getHeight());
    assertEquals(3, sm.getPlayCount());
    assertEquals(4, sm.getClickCount());
    assertEquals(1667029265129L, sm.getClientReportTime());
    assertEquals(1667025606646L, sm.getServerReportTime());
    assertEquals(1667026806983L, sm.getEntryReportLocalTime());

    ClicksGlobalCount cc = jdbcClient.sql("SELECT * FROM clicks_global_count").query(ClicksGlobalCount.class).single();
    assertEquals("00016e7b-8e8a-455a-86b0-b83f73edee44", cc.getCompanyKey());
    assertEquals("x", cc.getAggregateName());
    assertEquals(4, cc.getCount());
    assertTrue(cc.getModified() > 0);
    
    StatEvent se = jdbcClient.sql("SELECT * FROM stat_event").query(StatEvent.class).single();
    assertEquals(REPORT_ID, se.getReportId());
    assertEquals(1667026806983L, se.getReportTime());
    assertEquals("00016e7b-8e8a-455a-86b0-b83f73edee43", se.getScreenKey());
    assertEquals("-", se.getEventKey());
    assertEquals("type", se.getEventType());
  }
}
