package com.novisign.collector.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.apache.commons.codec.digest.DigestUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.novisign.collector.configuration.Configuration;
import com.novisign.collector.configuration.Configuration.QueueUrl;
import com.novisign.collector.service.MediaReportService;
import com.novisign.collector.service.SqsMessageSender;
import com.novisign.collector.service.StatPlayerInfoService;

@WebMvcTest(Controller.class)
public class ControllerTest {
  private final String token = DigestUtils.md5Hex("21" + Controller.SECRET);

  @MockitoBean
  Configuration config;
  @MockitoBean
  MediaReportService fileProcessingService;
  @MockitoBean
  SqsMessageSender sqsMessageSender;
  @MockitoBean
  StatPlayerInfoService statPlayerInfoService;
  @MockitoBean
  QueueUrl queueUrl;

  @Autowired
  MockMvc mockMvc;

  @Test
  public void getServerStatus() throws Exception {
    mockMvc.perform(get("/getServerStatus"))
      .andExpect(status().isOk())
      .andExpect(content().string("status: ok"));
  }

  @Test
  public void submitMediaReport_success() throws Exception {
    mockMvc.perform(put("/report/submit/media?rid=1&s=2&tk=" + token)
      .content(new byte[] { 1 }))
      .andExpect(status().isOk());

    verify(sqsMessageSender).sendMessage(any(), any());
  }

  @Test
  public void submitMediaReport_unauthorized() throws Exception {
    mockMvc.perform(put("/report/submit/media?rid=1&s=2&tk=0")
      .content(new byte[] { 1 }))
      .andExpect(status().isBadRequest());
  }

  @Test
  public void submitStatPlayerInfo_insert() throws Exception {
    mockMvc.perform(put("/monitor/submit/info?rid=1&s=2&tk=" + token)
      .content("i"))
      .andExpect(status().isOk());

    verify(statPlayerInfoService).create("i", "127.0.0.1");
  }

  @Test
  public void submitStatPlayerInfo_update() throws Exception {
    mockMvc.perform(put("/monitor/submit/info?rid=1&s=2&tk=" + token)
      .content("u"))
      .andExpect(status().isOk());

    verify(statPlayerInfoService).update("u", "127.0.0.1");
  }

  @Test
  public void submitStatPlayerInfo_unauthorized() throws Exception {
    mockMvc.perform(put("/monitor/submit/info?rid=1&s=2&tk=0")
    .content("i"))
    .andExpect(status().isBadRequest());
  }

  @Test
  public void updateStatPlayerInfoStatus() throws Exception {
    mockMvc.perform(put("/monitor/submit/status?rid=1&s=2&tk={tk}&status=started", token))
      .andExpect(status().isOk());

    verify(statPlayerInfoService).updateStatus("1", "started", "127.0.0.1");
  }

  @Test
  public void updateStatPlayerInfoStatus_unauthorized() throws Exception {
    mockMvc.perform(put("/monitor/submit/status?rid=1&s=2&tk=0&status=started"))
      .andExpect(status().isBadRequest());
  }
}
