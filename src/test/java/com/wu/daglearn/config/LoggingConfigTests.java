package com.wu.daglearn.config;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.File;
import java.nio.file.Files;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
public class LoggingConfigTests {

    private static final Logger log = LoggerFactory.getLogger(LoggingConfigTests.class);

    @Test
    @DisplayName("Given SLF4J Logger, when logging a message, then log message is successfully written to daily rotated log file")
    void givenSlf4jLogger_whenLoggingMessage_thenWrittenToLogFile() throws Exception {
        // Given - A unique test message
        String uniqueMessage = "TEST-LOG-MESSAGE-" + UUID.randomUUID().toString();
        File logFile = new File("logs/daglearn.log");

        // When - We log the unique message
        log.info(uniqueMessage);

        // Then - Verify log file is created and contains the logged message
        assertTrue(logFile.exists(), "Log file 'logs/daglearn.log' should be created by Logback configuration");
        
        boolean containsMessage = false;
        // Retry a few times to allow disk flush/write
        for (int i = 0; i < 5; i++) {
            String content = Files.readString(logFile.toPath());
            if (content.contains(uniqueMessage)) {
                containsMessage = true;
                break;
            }
            Thread.sleep(100);
        }

        assertTrue(containsMessage, "Log file should contain the unique message: " + uniqueMessage);
    }
}
