package com.xavelo.kafka;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Service;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.Acknowledgment;

@Service
public class KafkaListener {

    private static final Logger logger = LoggerFactory.getLogger(KafkaListener.class);

    //private final MongoAdapter mongoAdapter;
    private final ObjectMapper objectMapper;
    private final KafkaTemplate<String, String> kafkaTemplate;
    
    private static final int MAX_RETRIES = 3; 

    public KafkaListener(ObjectMapper objectMapper, KafkaTemplate<String, String> kafkaTemplate) {
        this.objectMapper = objectMapper;
        this.kafkaTemplate = kafkaTemplate;
    }

    @org.springframework.kafka.annotation.KafkaListener(topics = "test-topic", groupId = "test-group", containerFactory = "kafkaListenerContainerFactory")
    public void consume(ConsumerRecord<String, String> record, Acknowledgment acknowledgment)  {
        logger.info("Received kafka record: key {} - value {}, Offset: {}", record.key(), record.value(), record.offset());
        //process(record.value());
        acknowledgment.acknowledge();
    }

    // dummy processor to simple parse JSON messages and send to DLQ in case of error
    /*
    private void process(String message) {        
        int attempt = 0;

        while (attempt < MAX_RETRIES) {
            try {
                Message json = objectMapper.readValue(message, Message.class);

                mongoAdapter.checkCollection();
                mongoAdapter.saveMessage(json);
                mongoAdapter.findMessageByKey(json.getKey());

                logger.info("message successfully processed");
                return;

            } catch (JsonProcessingException e) {
                attempt++;
                logger.error("Attempt {}: error {} processing message {}", attempt, e.getMessage(), message);
                if (attempt >= MAX_RETRIES) {
                    sendToDLQ(message); // Send to DLQ after max retries
                } else {
                    // Exponential backoff delay
                    try {
                        Thread.sleep((long) Math.pow(2, attempt) * 1000); // Delay in milliseconds
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt(); // Restore interrupted status
                    }
                }
            }
        }
    }
    */

    // New method to send message to DLQ
    private void sendToDLQ(String message) {       
        kafkaTemplate.send("test-topic-dlq", message);
    }

}
