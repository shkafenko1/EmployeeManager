package by.koronatech.office.api.controller;

import by.koronatech.office.core.service.VisitCounterService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/visits")
@Tag(name = "Visit Counter API", description = "Операции со счетчиком посещений")
public class VisitCounterController {

    private static final Logger logger = LoggerFactory.getLogger(VisitCounterController.class);
    private final VisitCounterService visitCounterService;

    public VisitCounterController(VisitCounterService visitCounterService) {
        this.visitCounterService = visitCounterService;
    }

    @GetMapping("/count")
    @Operation(summary = "Получить счетчик посещений",
            description = "Возвращает количество посещений для указанного URL.")
    @ApiResponse(responseCode = "200", description = "Счетчик успешно получен")
    @ApiResponse(responseCode = "400", description = "Некорректный URL")
    public ResponseEntity<Long> getVisitCount(
            @Parameter(description = "URL для получения счетчика", required = true, example = "/api/departments")
            @RequestParam String url) {
        logger.info("Received request to get visit count for URL: {}", url);
        try {
            long count = visitCounterService.getVisitCount(url);
            return ResponseEntity.ok(count);
        } catch (IllegalArgumentException e) {
            logger.error("Invalid URL: {}", url, e);
            return ResponseEntity.badRequest().build();
        }
    }
}