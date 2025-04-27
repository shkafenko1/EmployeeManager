package by.koronatech.office.api.controller;

import by.koronatech.office.api.dto.DepartmentDto;
import by.koronatech.office.core.service.DepartmentService;
import by.koronatech.office.core.service.impl.EmployeeServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/departments")
@AllArgsConstructor
@Validated
@Tag(name = "Department API", description = "Операции для управления отделами")
public class DepartmentController {

    private final DepartmentService departmentService;
    private final EmployeeServiceImpl employeeService;

    @GetMapping
    @Operation(summary = "Получить список всех отделов",
            description = "Возвращает список всех существующих отделов.")

    @ApiResponse(responseCode = "200", description = "Список отделов успешно получен",
            content = @Content(mediaType = "application/json",
                    array = @ArraySchema(schema = @Schema(implementation = DepartmentDto.class))))

    public List<DepartmentDto> getAllDepartments() {
        return departmentService.getAllDepartments();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Получить отдел по ID",
            description =
                    "Возвращает детальную информацию об отделе по его уникальному идентификатору.")

    @ApiResponse(responseCode = "200", description = "Отдел найден и возвращен",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = DepartmentDto.class)))
    @ApiResponse(responseCode = "400", description = "Некорректный ID")
    @ApiResponse(responseCode = "404", description = "Отдел с указанным ID не найден")

    public DepartmentDto getDepartmentById(
            @Parameter(description = "ID отдела", required = true, example = "1")
            @PathVariable @Positive(message = "ID must be positive") Long id) {
        return departmentService.getDepartmentById(id);
    }

    @GetMapping("/unwrap")
    @Operation(summary = "Получить все отделы со списком их сотрудников",
            description = "Возвращает список всех отделов,"
                    + " где каждый отдел содержит также список своих сотрудников.")

    @ApiResponse(responseCode = "200",
            description = "Список отделов с сотрудниками успешно получен",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(type = "object",
                            example = "[{\"department\": {\"id\": 1, \"name\": \"IT\"}, "
                                    + "\"employees\": [{\"id\": 101, \"name\": \"John Doe\"}]}]")))
    @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера")

    public ResponseEntity<List<Map<String, Object>>> getAllDepartmentsWithEmployees() {
        try {
            List<DepartmentDto> departments = departmentService.getAllDepartments();
            List<Map<String, Object>> result = departments.stream()
                    .map(dept -> {
                        Map<String, Object> deptWithEmployees = new HashMap<>();
                        deptWithEmployees.put("department", dept);
                        deptWithEmployees.put("employees",
                                employeeService.findAllEmployeesByDepartment(dept.getName()));
                        return deptWithEmployees;
                    })
                    .toList();
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PostMapping
    @Operation(summary = "Создать новый отдел",
            description = "Создает новую запись об отделе. Требует указания существующей компании.")

    @ApiResponse(responseCode = "201", description = "Отдел успешно создан",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = DepartmentDto.class)))
    @ApiResponse(responseCode = "400", description = "Некорректные данные для создания отдела")
    @ApiResponse(responseCode = "404", description = "Связанная компания не найдена")

    public ResponseEntity<DepartmentDto> createDepartment(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Данные для создания нового отдела", required = true,
                    content = @Content(schema = @Schema(implementation = DepartmentDto.class)))
            @Valid @RequestBody DepartmentDto departmentDto) {
        DepartmentDto created = departmentService.createDepartment(departmentDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Обновить существующий отдел",
            description = "Обновляет информацию о существующем отделе по его ID.")

    @ApiResponse(responseCode = "200", description = "Отдел успешно обновлен",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = DepartmentDto.class)))
    @ApiResponse(responseCode = "400", description = "Некорректные данные или ID")
    @ApiResponse(responseCode = "404", description = "Отдел с указанным ID не найден")

    public DepartmentDto updateDepartment(
            @Parameter(description = "ID отдела для обновления", required = true, example = "1")
            @PathVariable @Positive(message = "ID must be positive") Long id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Обновленные данные отдела", required = true,
                    content = @Content(schema = @Schema(implementation = DepartmentDto.class)))
            @Valid @RequestBody DepartmentDto departmentDto) {
        return departmentService.updateDepartment(id, departmentDto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Удалить отдел",
            description = "Удаляет отдел по его уникальному идентификатору.")

    @ApiResponse(responseCode = "204", description = "Отдел успешно удален")
    @ApiResponse(responseCode = "400", description = "Некорректный ID")
    @ApiResponse(responseCode = "404", description = "Отдел с указанным ID не найден")

    public void deleteDepartment(
            @Parameter(description = "ID отдела для удаления", required = true, example = "1")
            @PathVariable @Positive(message = "ID must be positive") Long id) {
        departmentService.deleteDepartment(id);
    }
}