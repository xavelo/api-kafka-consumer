package com.xavelo.kafka;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class KafkaListener {

    private static final Logger logger = LoggerFactory.getLogger(KafkaListener.class);

    private final MongoAdapter mongoAdapter;
    private final ObjectMapper objectMapper;

    public KafkaListener(MongoAdapter mongoAdapter, ObjectMapper objectMapper) {
        this.mongoAdapter = mongoAdapter;
        this.objectMapper = objectMapper;
    }

    @org.springframework.kafka.annotation.KafkaListener(topics = "test-topic", groupId = "test-group", containerFactory = "kafkaListenerContainerFactory")
    public void consume(String message) throws JsonProcessingException {
        logger.info("Received message: {}", message);   
        Message json = objectMapper.readValue(message, Message.class);     
        mongoAdapter.checkCollection();
        mongoAdapter.saveMessage(json);
        mongoAdapter.findMessageByKey(json.getKey());
    }

}

