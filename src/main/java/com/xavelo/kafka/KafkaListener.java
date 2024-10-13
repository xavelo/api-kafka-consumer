package com.xavelo.kafka;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Service;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;

@Service
public class KafkaListener {

    private static final Logger logger = LoggerFactory.getLogger(KafkaListener.class);

    private final MongoAdapter mongoAdapter;
    private final ObjectMapper objectMapper;
    private final KafkaTemplate<String, String> kafkaTemplate;

    @Value("${spring.kafka.consumer.max-retries}")
    private int MAX_RETRIES; 

    public KafkaListener(MongoAdapter mongoAdapter, ObjectMapper objectMapper, KafkaTemplate<String, String> kafkaTemplate) {
        this.mongoAdapter = mongoAdapter;
        this.objectMapper = objectMapper;
        this.kafkaTemplate = kafkaTemplate;
    }

    @org.springframework.kafka.annotation.KafkaListener(topics = "test-topic", groupId = "test-group", containerFactory = "kafkaListenerContainerFactory")
    public void consume(ConsumerRecord<String, String> record)  {
        logger.info("Received kafka record: key{} - value {}, Offset: {}", record.key(), record.value(), record.offset());
        process(record.value());
        logger.info("message successfully processed");
    }

    // dummy processor to simple parse JSON messages and send to DLQ in case of error
    private void process(String message) {        
        int attempt = 0;

        while (attempt < MAX_RETRIES) {
            try {
                Message json = objectMapper.readValue(message, Message.class);

                mongoAdapter.checkCollection();
                mongoAdapter.saveMessage(json);
                mongoAdapter.findMessageByKey(json.getKey());
                return; // Exit if processing is successful

            } catch (JsonProcessingException e) {
                attempt++;
                logger.error("Attempt {}: error {} processing message {}", attempt, e.getMessage(), message);
                if (attempt >= MAX_RETRIES) {
                    sendToDLQ(message); // Send to DLQ after max retries
                }
            }
        }
    }

    // New method to send message to DLQ
    private void sendToDLQ(String message) {       
        kafkaTemplate.send("test-topic-dlq", message);
    }

}
