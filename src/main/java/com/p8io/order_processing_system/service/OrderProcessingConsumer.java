package com.p8io.order_processing_system.service;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class OrderProcessingConsumer {

    Logger log = LoggerFactory.getLogger(OrderProcessingConsumer.class);

    @KafkaListener(topics = "${app.kafka.order-topic}", groupId = "order-processing-group")
    public void listen(ConsumerRecord<String, String> record) {
        log.info("Received message from topic '{}' partition {} offset {}: Key = {}, Value = {}",
                record.topic(), record.partition(), record.offset(), record.key(), record.value());
        String recordValue = record.value();
        log.info("record got from :: {}", recordValue);
    }
}
