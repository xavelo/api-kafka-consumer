package com.xavelo.kafka;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.info.GitProperties;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class KafkaConsumerController {

    private static final Logger logger = LogManager.getLogger(KafkaConsumerController.class);

    @Value("${HOSTNAME:unknown}")
    private String podName;

    @Autowired
    private KafkaListener kafkaListener;

    @GetMapping("/ping")
    public ResponseEntity<String> ping() {
        return ResponseEntity.ok("ping from pod " + podName);
    }

    @GetMapping("/consume")
    public ResponseEntity<String> consume(@RequestParam String topic) {
        logger.info("consuming messages from topic {}", topic);
        return ResponseEntity.ok(topic);
    }

}

