package com.xavelo.kafka.application;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

@Service
public class ExpensiveOperationService {

    private static final Logger logger = LogManager.getLogger(ExpensiveOperationService.class);

    public void simulateExpensiveOperation() {
        logger.info("Starting expensive operation...");
        long start = System.nanoTime();
        // Simulate an expensive computational operation, e.g., calculating primes
        long primes = 0;
        for (int i = 2; i < 10000000; i++) {
            if (isPrime(i)) {
                primes++;
            }
        }
        long end = System.nanoTime();
        logger.info("Found " + primes + " primes in " + (end - start) / 1_000_000 + " ms.");
    }

    private boolean isPrime(int number) {
        if (number <= 1) return false;
        for (int i = 2; i <= Math.sqrt(number); i++) {
            if (number % i == 0) return false;
        }
        return true;
    }

}
