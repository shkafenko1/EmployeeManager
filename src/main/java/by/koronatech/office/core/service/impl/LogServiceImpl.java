package by.koronatech.office.core.service.impl;

import by.koronatech.office.core.service.LogService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service public class LogServiceImpl implements LogService {

    private static final Logger logger = LoggerFactory.getLogger(LogServiceImpl.class);
    private final Map<String, String> logStatus = new ConcurrentHashMap<>();
    private final Map<String, Path> logFiles = new ConcurrentHashMap<>();
    private static final String LOG_DIR = "D:\\Java Projects\\EmployeeManager\\logs";
    private static final String APP_LOG = "D:\\Java Projects\\EmployeeManager\\logs\\app.log";
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Override
    public String initiateLogFileCreation(LocalDate date) {
        if (date == null) {
            throw new IllegalArgumentException("Date cannot be null");
        }
        String id = UUID.randomUUID().toString();
        logStatus.put(id, "PROCESSING");
        logger.info("Initiating log file creation for ID: {} and date: {} on thread: {}",
                id, date, Thread.currentThread().getName());
        createLogFileAsync(id, date);
        return id;
    }

    @Async("taskExecutor")
    public void createLogFileAsync(String id, LocalDate date) {
        logger.info("Starting async log file creation for ID: {} and date: {} on thread: {}",
                id, date, Thread.currentThread().getName());
        CompletableFuture.runAsync(() -> {
            try {
                Path dir = Paths.get(LOG_DIR);
                logger.debug("Checking directory: {}", dir.toAbsolutePath());
                if (!Files.exists(dir)) {
                    logger.info("Creating directory: {}", dir);
                    Files.createDirectories(dir);
                }
                String fileName = "log-" + date.format(DATE_FORMATTER) + ".log";
                Path filePath = dir.resolve(fileName);
                logger.debug("Writing to file: {}", filePath.toAbsolutePath());

                String datePrefix = date.format(DATE_FORMATTER);
                String content;
                Path appLogPath = Paths.get(APP_LOG);
                if (Files.exists(appLogPath)) {
                    content = Files.lines(appLogPath)
                            .filter(line -> line.startsWith(datePrefix))
                            .collect(Collectors.joining("\n"));
                    if (content.isEmpty()) {
                        content = "No log entries found for " + date;
                    }
                } else {
                    content = "App log file not found for " + date;
                }

                logger.debug("Simulating long process for ID: {} on thread: {}", id, Thread.currentThread().getName());
                Thread.sleep(15000);

                Files.writeString(filePath, content);
                logFiles.put(id, filePath);
                logStatus.put(id, "CREATED");
                logger.info("Log file created for ID: {} and date: {} on thread: {}",
                        id, date, Thread.currentThread().getName());
            } catch (IOException | InterruptedException e) {
                logStatus.put(id, "FAILED");
                logger.error("Failed to create log file for ID: {}. Path: {}. Date: {}. Error: {}",
                        id, Paths.get(LOG_DIR).toAbsolutePath(), date, e.getMessage(), e);
                throw new RuntimeException("Failed to create log file: " + e.getMessage(), e);
            }
        });
    }

    @Override
    public String getLogFileStatus(String id) {
        String status = logStatus.getOrDefault(id, "NOT_FOUND");
        logger.debug("Returning status for ID: {}: {} on thread: {}", id, status, Thread.currentThread().getName());
        return status;
    }

    @Override
    public Path getLogFilePath(String id) {
        Path path = logFiles.get(id);
        logger.debug("Returning file path for ID: {}: {} on thread: {}", id, path, Thread.currentThread().getName());
        return path;
    }

}