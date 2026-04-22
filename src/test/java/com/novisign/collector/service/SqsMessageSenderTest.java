package com.novisign.collector.service;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.concurrent.CompletableFuture;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.novisign.collector.configuration.Configuration.QueueUrl;

import software.amazon.awssdk.services.sqs.SqsAsyncClient;
import software.amazon.awssdk.services.sqs.model.MessageAttributeValue;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;
import software.amazon.awssdk.services.sqs.model.SendMessageResponse;

@ExtendWith(MockitoExtension.class)
class SqsMessageSenderTest {
  static String reportId = "1667025606646_a_b23de1ed2b00b1f14741849adb25f889_82117697409995960.mrps.gz";
  static byte[] reportContent = {1, 2, 3};

  
  @Mock SqsAsyncClient sqsClient;
  @Mock QueueUrl queueUrl;

  @InjectMocks
  private SqsMessageSender sqsMessageSender;

  @Test
  void sendMessage_Success() {
    SendMessageResponse response = SendMessageResponse.builder().build();
    when(sqsClient.sendMessage(any(SendMessageRequest.class))).thenReturn(CompletableFuture.completedFuture(response));

    sqsMessageSender.sendMessage(reportId, reportContent);

    ArgumentCaptor<SendMessageRequest> requestCaptor = ArgumentCaptor.forClass(SendMessageRequest.class);
    verify(sqsClient).sendMessage(requestCaptor.capture());
    SendMessageRequest request = requestCaptor.getValue();
    assertEquals(request.messageBody(), reportId);

    MessageAttributeValue attributeValue = request.messageAttributes().get("Content");
    assertEquals(attributeValue.dataType(), "Binary");
    assertArrayEquals(attributeValue.binaryValue().asByteArray(), reportContent);
  }

  @Test
  void sendMessage_Failure() {
    CompletableFuture<SendMessageResponse> future = new CompletableFuture<>();
    future.completeExceptionally(new RuntimeException());
    when(sqsClient.sendMessage(any(SendMessageRequest.class))).thenReturn(future);

    sqsMessageSender.sendMessage(reportId, reportContent);
    
    verify(sqsClient).sendMessage(any(SendMessageRequest.class));
  }
}
