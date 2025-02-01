package com.xavelo.kafka;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

//@Component
public class MongoAdapter {

    private static final Logger logger = LoggerFactory.getLogger(MongoAdapter.class);

    private final MongoTemplate mongoTemplate;

    public MongoAdapter(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    public void saveMessage(Message message) {
        mongoTemplate.save(message, "test_topic");
        logger.info("Message with key {} saved",  message.getKey());
    }

    public Message findMessageByKey(String key) {
        Message msg = mongoTemplate.findOne(Query.query(Criteria.where("key").is(key)), Message.class, "test_topic");
        return msg;
    }

    public void checkCollection() {
        if (!mongoTemplate.collectionExists("test-topic")) {
            logger.info("Creating collection test-topic");
            mongoTemplate.createCollection("test-topic");
        } else {
            logger.info("Collection test-topic already existing");
        }
    }
    
}
