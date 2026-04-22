package com.novisign.collector.configuration;

import java.util.concurrent.ExecutionException;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.services.sqs.SqsAsyncClient;
import software.amazon.awssdk.services.sqs.model.GetQueueUrlRequest;

@ConfigurationProperties(prefix = "collector")
@Getter
@Slf4j
@RequiredArgsConstructor
public class Configuration {
  private final String queueName;
  private final String bucketName;
  
  @RequiredArgsConstructor
  public static class QueueUrl {
    private final String queueUrl;
   
    public String get() {
      return queueUrl;
    }
    
    @Override
    public String toString() {
      return queueUrl;
    }
  }
  
  @Bean
  public QueueUrl queueUrl(SqsAsyncClient sqsClient) throws InterruptedException, ExecutionException {
    log.debug("Getting queueUrl for {} queue", queueName);
    String queueUrl = sqsClient.getQueueUrl(GetQueueUrlRequest.builder().queueName(queueName).build()).get().queueUrl();
    return new QueueUrl(queueUrl);
  }
}
