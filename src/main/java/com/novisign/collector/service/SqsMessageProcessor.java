package com.novisign.collector.service;

import org.springframework.stereotype.Service;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.novisign.collector.configuration.Configuration;

import io.awspring.cloud.sqs.annotation.SqsListener;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.sqs.model.Message;

@Service
@RequiredArgsConstructor
@Slf4j
public class SqsMessageProcessor {
  public static final String CONTENT_ATTR_NAME = "Content";

  private final Cache<String, String> messageIdCache = Caffeine.newBuilder().maximumSize(100000).build();

  private final Configuration config;
  private final S3Client s3Client;
  private final MediaReportService statReportService;

  @SqsListener("${collector.queue-name}")
  public void processMessage(Message message) {
    if (messageIdCache.asMap().containsKey(message.messageId())) {
      log.debug("Skipping duplicate message {}", message.messageId());
      return;
    }
    log.debug("Processing message {}", message.messageId());
    try {
      byte[] content = message.messageAttributes().get(CONTENT_ATTR_NAME).binaryValue().asByteArray();
      statReportService.processMediaReport(message.body(), content);
      sendToS3(message.body(), content);
      messageIdCache.put(message.messageId(), message.messageId());
    } catch (Exception e) {
      log.error("Failed to process message {}", message.messageId(), e);
    }
  }

  private void sendToS3(String reportId, byte[] content) {
    log.debug("Sending {} to S3", reportId);
    s3Client.putObject(PutObjectRequest.builder()
      .bucket(config.getBucketName())
      .key(reportId)
      .build(), 
      RequestBody.fromBytes(content));
  }
}

