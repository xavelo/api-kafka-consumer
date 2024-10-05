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

    private final MongoTemplate mongoTemplate;
    private final ObjectMapper objectMapper;

    public KafkaListener(MongoTemplate mongoTemplate, ObjectMapper objectMapper) {
        this.mongoTemplate = mongoTemplate;
        this.objectMapper = objectMapper;
    }

    @org.springframework.kafka.annotation.KafkaListener(topics = "test-topic", groupId = "test-group", containerFactory = "kafkaListenerContainerFactory")
    public void consume(String message) throws JsonProcessingException {
        logger.info("Received message: {}", message);   
        Message json = objectMapper.readValue(message, Message.class);     
        checkCollection();
        saveMessage(json);
        findMessageByKey(json.getKey());
    }

    private void checkCollection() {
        if (!mongoTemplate.collectionExists("test-topic")) {
            logger.info("Creating collection test-topic");
            mongoTemplate.createCollection("test-topic");
        } else {
            logger.info("Collection test-topic already existing");
        }
    }

    private void saveMessage(Message message) {
        mongoTemplate.save(message, "test_topic");
        logger.info("Message with key {} saved",  message.getKey());
    }

    private Message findMessageByKey(String key) {
        Message msg = mongoTemplate.findOne(Query.query(Criteria.where("key").is(key)), Message.class, "test_topic");
        return msg;
    }

}

