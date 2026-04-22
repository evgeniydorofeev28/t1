package com.novisign.collector.service;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.novisign.collector.configuration.Configuration.QueueUrl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.services.sqs.SqsAsyncClient;
import software.amazon.awssdk.services.sqs.model.MessageAttributeValue;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;

@Service
@RequiredArgsConstructor
@Slf4j
public class SqsMessageSender {
    private final SqsAsyncClient sqsClient;
    private final QueueUrl queueUrl;
    
    public void sendMessage(String reportId, byte[] body) {
      log.debug("Sending {} to SQS", reportId);
      MessageAttributeValue attributeValue = MessageAttributeValue.builder()
        .dataType("Binary")
        .binaryValue(SdkBytes.fromByteArray(body))
        .build();
      SendMessageRequest request = SendMessageRequest.builder()
        .queueUrl(queueUrl.get())
        .messageBody(reportId)
        .messageAttributes(Map.of(SqsMessageProcessor.CONTENT_ATTR_NAME, attributeValue))
        .build();
      sqsClient.sendMessage(request);
    }
}
