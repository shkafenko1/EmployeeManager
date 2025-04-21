package by.koronatech.office.api.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/logs")
@Tag(name = "Log API", description = "Операции с логами приложения")
public class LogController {

    private static final Logger logger = LoggerFactory.getLogger(LogController.class);

    @GetMapping
    @Operation(summary = "Получить лог-файл по дате",
            description = "Возвращает лог-файл за указанную дату в формате YYYY-MM-DD.")

    @ApiResponse(responseCode = "200", description = "Лог-файл успешно получен",
            content = @Content(mediaType = MediaType.TEXT_PLAIN_VALUE))
    @ApiResponse(responseCode = "400", description = "Некорректный формат даты")
    @ApiResponse(responseCode = "404", description = "Лог-файл за указанную дату не найден")
    @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера")

    public ResponseEntity<Resource> getLogFile(
            @Parameter(description = "Дата логов (YYYY-MM-DD)",
                    required = true, example = "2025-04-13")
            @RequestParam("date") String date) {
        logger.info("Requesting log file for date: {}", date);
        try {
            LocalDate logDate = LocalDate.parse(date, DateTimeFormatter.ISO_LOCAL_DATE);
            String fileName = String.format("logs/app.%s.log",
                    logDate.format(DateTimeFormatter.ISO_LOCAL_DATE));
            Path filePath = Paths.get(fileName);

            if (!Files.exists(filePath)) {
                logger.error("(HTTP 404)Log file not found for date: {}", date);
                return ResponseEntity.notFound().build();
            }

            File file = filePath.toFile();
            InputStreamResource resource = new InputStreamResource(new FileInputStream(file));

            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + file.getName());

            logger.info("Returning log file: {}", fileName);
            return ResponseEntity.ok()
                    .headers(headers)
                    .contentLength(file.length())
                    .contentType(MediaType.TEXT_PLAIN)
                    .body(resource);

        } catch (DateTimeParseException e) {
            logger.error("(HTTP 400)Invalid date format: {}", date);
            return ResponseEntity.badRequest().body(null);
        } catch (IOException e) {
            logger.error("Error reading log file (HTTP 500) for date: {}", date, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
}