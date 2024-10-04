package com.xavelo.kafka;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class KafkaConsumerController {

    private static final Logger logger = LogManager.getLogger(KafkaConsumerController.class);

    @Value("${HOSTNAME:unknown}")
    private String podName;

    @GetMapping("/ping")
    public ResponseEntity<String> ping() {
        logger.info("ping from pod {}", podName);
        return ResponseEntity.ok("ping from pod " + podName);
    }

}

