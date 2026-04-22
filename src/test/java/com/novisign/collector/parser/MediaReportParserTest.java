package com.novisign.collector.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import com.novisign.collector.model.ClicksGlobalCount;
import com.novisign.collector.model.StatEvent;
import com.novisign.collector.model.StatMedia;

public class MediaReportParserTest {
  private static final String REPORT_ID = "1667025606646_a_b23de1ed2b00b1f14741849adb25f889_82117697409995960.mrps.gz";

  @Test
  public void parseHeader() {
    StatReportHeader h = MediaReportParser.parseHeader("h;1;rid1;1397082471958;Asia/Jerusalem;\n");

    assertEquals("rid1", h.getReporterId());
    assertEquals(1397082471958L, h.getReporterTime());
    assertEquals("Asia/Jerusalem", h.getReporterTimezone());
    assertEquals(10800000, h.getReporterTimezoneOffset());
  }

  @Test
  public void parseEvent() {
    StatEvent se = MediaReportParser.parseEvent("e;1;1667026806983;00016e7b-8e8a-455a-86b0-b83f73edee43;type;1;", REPORT_ID);

    assertEquals(REPORT_ID, se.getReportId());
    assertEquals(1667026806983L, se.getReportTime());
    assertEquals("00016e7b-8e8a-455a-86b0-b83f73edee43", se.getScreenKey());
    assertEquals("-", se.getEventKey());
    assertEquals("type", se.getEventType());
  }

  @Test
  public void parseMediaV1() {
    StatReportHeader h = new StatReportHeader();
    h.setReporterId("1");
    h.setReporterTime(1667029265129L);
    h.setReporterTimezone("GMT");

    StatMedia sm = MediaReportParser.parseMedia("m;1;1667026806983;91330562-68c4-4b8c-984b-67e292141ac7;3c4223c7-7033-45c9-9bde-d0ea7c088f34;-;e17cc281-1989-4922-8f41-cecc4ca92633.cnt;-;1;2;19802318;3;", REPORT_ID, h);

    assertEquals(REPORT_ID, sm.getReportId());
    assertEquals(1667026806983L, sm.getEntryReportTime());
    assertEquals("91330562-68c4-4b8c-984b-67e292141ac7", sm.getScreenKey());
    assertEquals("3c4223c7-7033-45c9-9bde-d0ea7c088f34", sm.getMainPlaylistKey());
    assertEquals("-", sm.getPlaylistKey());
    assertEquals("e17cc281-1989-4922-8f41-cecc4ca92633.cnt", sm.getCreativeKey());
    assertEquals("-", sm.getMediaKey());
    assertEquals("", sm.getWidgetName());
    assertEquals("GMT", sm.getTimezoneId());
    assertEquals(0, sm.getTimezoneOffset());
    assertEquals(19802318, sm.getPlayDuration());
    assertEquals(1, sm.getWidth());
    assertEquals(2, sm.getHeight());
    assertEquals(3, sm.getPlayCount());
    assertEquals(0, sm.getClickCount());
    assertEquals(1667029265129L, sm.getClientReportTime());
    assertEquals(1667025606646L, sm.getServerReportTime());
    assertEquals(1667026806983L, sm.getEntryReportLocalTime());
  }

  @Test
  public void parseMediaV2() {
    StatReportHeader h = new StatReportHeader();
    h.setReporterId("1");
    h.setReporterTime(1667029265129L);
    h.setReporterTimezone("GMT");

    StatMedia sm = MediaReportParser.parseMedia("m;2;1667026806983;91330562-68c4-4b8c-984b-67e292141ac7;3c4223c7-7033-45c9-9bde-d0ea7c088f34;-;e17cc281-1989-4922-8f41-cecc4ca92633.cnt;-;widget;1;2;19802318;3;4;", REPORT_ID, h);

    assertEquals(REPORT_ID, sm.getReportId());
    assertEquals(1667026806983L, sm.getEntryReportTime());
    assertEquals("91330562-68c4-4b8c-984b-67e292141ac7", sm.getScreenKey());
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
  }

  @Test
  public void parseMediaV3() {
    StatReportHeader h = new StatReportHeader();
    h.setReporterId("1");
    h.setReporterTime(1667029265129L);
    h.setReporterTimezone("GMT");

    StatMedia sm = MediaReportParser.parseMedia("m;3;1667026806983;91330562-68c4-4b8c-984b-67e292141ac7;3c4223c7-7033-45c9-9bde-d0ea7c088f34;-;e17cc281-1989-4922-8f41-cecc4ca92633.cnt;-;widget;1;2;aggregateClicksName=x;19802318;3;4;", REPORT_ID, h);

    assertEquals(REPORT_ID, sm.getReportId());
    assertEquals(1667026806983L, sm.getEntryReportTime());
    assertEquals("91330562-68c4-4b8c-984b-67e292141ac7", sm.getScreenKey());
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
    assertEquals("aggregateClicksName=x", sm.getFlags());
    assertEquals(1667026806983L, sm.getEntryReportLocalTime());
  }

  @Test
  public void parseClicksGlobalCount_noFlags() {
    StatMedia sm = new StatMedia();

    ClicksGlobalCount cc = MediaReportParser.parseClicksGlobalCount(sm);

    assertNull(cc);
  }

  @Test
  public void parseClicksGlobalCount() {
    StatMedia sm = new StatMedia();
    sm.setScreenKey("91330562-68c4-4b8c-984b-67e292141ac7");
    sm.setFlags("aggregateClicksName=x");
    sm.setClickCount(1);

    ClicksGlobalCount cc = MediaReportParser.parseClicksGlobalCount(sm);

    assertNotNull(cc);
    assertEquals("x", cc.getAggregateName());
    assertEquals("91330562-68c4-4b8c-984b-67e292141ac7", cc.getScreenKey());
    assertEquals(1, cc.getCount());
    assertTrue(cc.getModified() > 0);
  }
}
