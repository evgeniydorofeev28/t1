package com.novisign.collector.service;

import static org.mockito.Mockito.verify;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.novisign.collector.configuration.Configuration;

import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.Message;
import software.amazon.awssdk.services.sqs.model.MessageAttributeValue;

@ExtendWith(MockitoExtension.class)
public class SqsMessageProcessorTest {
    @Mock Configuration config;
    @Mock SqsClient sqsClient;
    @Mock S3Client s3Client;
    @Mock MediaReportService reportService;

    @InjectMocks
    SqsMessageProcessor sqsMessageProcessor;

    @Test
    void processMessage() throws Exception {
        String reportId = "1667025606646_a_b23de1ed2b00b1f14741849adb25f889_82117697409995960.mrps.gz";
        byte[] content = {1, 2, 3};

        Map<String, MessageAttributeValue> attributes = new HashMap<>();
        attributes.put(SqsMessageProcessor.CONTENT_ATTR_NAME, MessageAttributeValue.builder()
          .binaryValue(SdkBytes.fromByteArray(content))
          .build());

        Message message = Message.builder()
          .body(reportId)
          .messageId("1")
          .messageAttributes(attributes)
          .build();

        sqsMessageProcessor.processMessage(message);

        verify(reportService).processMediaReport(reportId, content);
    }
}
