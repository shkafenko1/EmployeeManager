package by.koronatech.office.api.controller;

import by.koronatech.office.core.service.LogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;

@RestController
@RequestMapping("/api/logs")
@Tag(name = "Log API", description = "Операции с лог-файлами")
public class LogController {

    private static final Logger logger = LoggerFactory.getLogger(LogController.class);
    private final LogService logService;

    public LogController(LogService logService) {
        this.logService = logService;
    }

    @PostMapping("/create")
    @Operation(summary = "Создать лог-файл за дату асинхронно",
            description = "Инициирует создание лог-файла за указанную дату и возвращает его ID.")
    @ApiResponse(responseCode = "200", description = "ID лог-файла возвращен")
    @ApiResponse(responseCode = "400", description = "Некорректная дата")
    @ApiResponse(responseCode = "500", description = "Ошибка сервера")
    public ResponseEntity<String> createLogFile(
            @Parameter(description = "Дата в формате YYYY-MM-DD", required = true)
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        logger.info("Request to create log file for date: {} on thread: {}", date, Thread.currentThread().getName());
        if (date == null) {
            logger.error("Date is null");
            return ResponseEntity.badRequest().body("Date cannot be null");
        }
        if (date.isAfter(LocalDate.now())) {
            logger.error("Date is in the future: {}", date);
            return ResponseEntity.badRequest().body("Date cannot be in the future");
        }
        logger.debug("Initiating async log file creation for date: {} on thread: {}", date, Thread.currentThread().getName());

        try {
            String id = logService.initiateLogFileCreation(date);
            logger.info("Returning ID: {} for date: {} on thread: {}", id, date, Thread.currentThread().getName());
            return ResponseEntity.ok(id);
        } catch (Exception e) {
            logger.error("Error initiating log file creation for date {}: {} on thread: {}",
                    date, e.getMessage(), Thread.currentThread().getName(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to initiate log creation: " + e.getMessage());
        }
    }

    @GetMapping("/status/{id}")
    @Operation(summary = "Получить статус лог-файла",
            description = "Возвращает статус лог-файла по его ID (PROCESSING, CREATED, FAILED, NOT_FOUND).")
    @ApiResponse(responseCode = "200", description = "Статус возвращен")
    public ResponseEntity<String> getLogFileStatus(
            @Parameter(description = "ID лог-файла", required = true)
            @PathVariable String id) {
        logger.info("Request to get status for log file ID: {} on thread: {}", id, Thread.currentThread().getName());
        String status = logService.getLogFileStatus(id);
        return ResponseEntity.ok(status);
    }

    @GetMapping("/download/{id}")
    @Operation(summary = "Скачать лог-файл",
            description = "Возвращает лог-файл по его ID.")
    @ApiResponse(responseCode = "200", description = "Лог-файл возвращен",
            content = @Content(mediaType = MediaType.TEXT_PLAIN_VALUE))
    @ApiResponse(responseCode = "404", description = "Лог-файл не найден")
    @ApiResponse(responseCode = "500", description = "Ошибка сервера")
    public ResponseEntity<Resource> downloadLogFile(
            @Parameter(description = "ID лог-файла", required = true)
            @PathVariable String id) {
        logger.info("Request to download log file ID: {} on thread: {}", id, Thread.currentThread().getName());
        Path filePath = logService.getLogFilePath(id);
        if (filePath == null || !Files.exists(filePath)) {
            logger.error("Log file not found for ID: {}", id);
            return ResponseEntity.notFound().build();
        }

        try {
            File file = filePath.toFile();
            InputStreamResource resource = new InputStreamResource(new FileInputStream(file));
            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + file.getName());

            logger.info("Returning log file for ID: {} on thread: {}", id, Thread.currentThread().getName());
            return ResponseEntity.ok()
                    .headers(headers)
                    .contentLength(file.length())
                    .contentType(MediaType.TEXT_PLAIN)
                    .body(resource);
        } catch (IOException e) {
            logger.error("Error reading log file for ID: {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
}