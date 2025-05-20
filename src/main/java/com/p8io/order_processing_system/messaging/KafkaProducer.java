package com.p8io.order_processing_system.messaging;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Component
public class KafkaProducer {
    private static final Logger log = LoggerFactory.getLogger(KafkaProducer.class);

    private final KafkaTemplate<String, String> kafkaTemplate;

    public KafkaProducer(KafkaTemplate<String, String> kafkaTemplate, ObjectMapper objectMapper) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void send(String topic, String key, String jsonPayload) throws ExecutionException, InterruptedException {
        try {
            CompletableFuture<SendResult<String, String>> sendFuture = kafkaTemplate.send(topic, key, jsonPayload);
            SendResult<String, String> result = sendFuture.get();
            log.info("Message with key '{}' buffered successfully for topic '{}' at offset {} on partition {}.",
                    key, topic,
                    result.getRecordMetadata().offset(), result.getRecordMetadata().partition());
        } catch (InterruptedException | ExecutionException e) {
            log.error("Failed to buffer/prepare message with key '{}' for topic '{}'", key, topic, e);
            throw new RuntimeException("Failed to prepare message for Kafka send", e);
        }

    }
}
