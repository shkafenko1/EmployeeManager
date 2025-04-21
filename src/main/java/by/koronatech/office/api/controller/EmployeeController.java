package by.koronatech.office.api.controller;

import by.koronatech.office.api.dto.CreateEmployeeDto;
import by.koronatech.office.api.dto.EmployeeDto;
import by.koronatech.office.api.dto.UpdateDto;
import by.koronatech.office.core.service.impl.EmployeeServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/employee")
@AllArgsConstructor
@Validated
@Tag(name = "Employee API", description = "Операции для управления сотрудниками")
public class EmployeeController {

    private final EmployeeServiceImpl employeeService;

    @GetMapping
    @Operation(summary = "Получить список всех сотрудников",
            description = "Возвращает список всех зарегистрированных сотрудников.")
    @ApiResponse(responseCode = "200", description = "Список сотрудников успешно получен",
            content = @Content(mediaType = "application/json",
                    array = @ArraySchema(schema = @Schema(implementation = EmployeeDto.class))))
    public List<EmployeeDto> getAllEmployees() {
        return employeeService.getAllEmployees();
    }

    @PostMapping("/create")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Создать нового сотрудника",
            description = "Создает нового сотрудника с указанными данными.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Сотрудник успешно создан",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = EmployeeDto.class))),
            @ApiResponse(responseCode = "400", description = "Некорректные данные")
    })
    public EmployeeDto createEmployee(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Данные для создания сотрудника", required = true,
                    content = @Content(schema = @Schema(implementation = CreateEmployeeDto.class)))
            @Valid @RequestBody CreateEmployeeDto employeeDto) {
        return employeeService.createEmployee(employeeDto);
    }

    @PostMapping("/bulk")
    @Operation(summary = "Массовое создание сотрудников",
            description = "Создает несколько сотрудников за один запрос. Возвращает список успешно созданных сотрудников и ошибки валидации.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Результаты обработки сотрудников",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Map.class))),
            @ApiResponse(responseCode = "400", description = "Некорректные данные в запросе")
    })
    public ResponseEntity<Map<String, Object>> bulkCreateEmployees(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Список данных для создания сотрудников", required = true,
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = CreateEmployeeDto.class))))
            @Valid @RequestBody List<CreateEmployeeDto> employeeDtos) {
        Map<String, Object> result = employeeService.bulkCreateEmployees(employeeDtos);
        return ResponseEntity.ok(result);
    }

    @GetMapping(params = "department")
    @Operation(summary = "Найти сотрудников по отделу",
            description = "Возвращает сотрудников в указанном отделе.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Сотрудники найдены",
                    content = @Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = EmployeeDto.class)))),
            @ApiResponse(responseCode = "400", description = "Некорректное название отдела")
    })
    public List<EmployeeDto> findAllEmployeesByDepartment(
            @Parameter(description = "Название отдела", required = true, example = "Разработка")
            @RequestParam("department") @NotBlank(message = "Department name cannot be empty") String department) {
        return employeeService.findAllEmployeesByDepartment(department);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Обновить сотрудника",
            description = "Обновляет данные сотрудника по его ID.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Сотрудник успешно обновлен",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = EmployeeDto.class))),
            @ApiResponse(responseCode = "400", description = "Некорректные данные или ID"),
            @ApiResponse(responseCode = "404", description = "Сотрудник с указанным ID не найден")
    })
    public EmployeeDto updateEmployee(
            @Parameter(description = "ID сотрудника", required = true, example = "1")
            @PathVariable @Positive(message = "ID must be positive") Long id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Обновленные данные сотрудника", required = true,
                    content = @Content(schema = @Schema(implementation = UpdateDto.class)))
            @Valid @RequestBody UpdateDto employeeDto) {
        return employeeService.updateEmployee(id, employeeDto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Удалить сотрудника",
            description = "Удаляет сотрудника по его ID.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Сотрудник успешно удален"),
            @ApiResponse(responseCode = "400", description = "Некорректный ID"),
            @ApiResponse(responseCode = "404", description = "Сотрудник с указанным ID не найден")
    })
    public void deleteEmployee(
            @Parameter(description = "ID сотрудника", required = true, example = "1")
            @PathVariable @Positive(message = "ID must be positive") Long id) {
        employeeService.deleteEmployee(id);
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Получить сотрудника по ID",
            description = "Возвращает данные сотрудника по его ID.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Сотрудник найден",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = EmployeeDto.class))),
            @ApiResponse(responseCode = "400", description = "Некорректный ID"),
            @ApiResponse(responseCode = "404", description = "Сотрудник с указанным ID не найден")
    })
    public EmployeeDto findEmployeeById(
            @Parameter(description = "ID сотрудника", required = true, example = "1")
            @PathVariable @Positive(message = "ID must be positive") Long id) {
        return employeeService.findEmployeeById(id);
    }
}