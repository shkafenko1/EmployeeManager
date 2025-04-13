package by.koronatech.office.api.controller;

import by.koronatech.office.api.dto.CompanyDto;
import by.koronatech.office.api.dto.EmployeeDto;
import by.koronatech.office.core.service.CompanyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/company")
@AllArgsConstructor
@Validated
@Tag(name = "Company API", description = "Операции для управления компаниями и связанными данными")
public class CompanyController {

    private final CompanyService companyService;

    @GetMapping
    @Operation(summary = "Получить список всех компаний",
            description = "Возвращает список всех зарегистрированных компаний.")

    @ApiResponse(responseCode = "200", description = "Список компаний успешно получен",
            content = @Content(mediaType = "application/json",
                    array = @ArraySchema(schema = @Schema(implementation = CompanyDto.class))))

    public List<CompanyDto> getAllCompanies() {
        return companyService.getAllCompanies();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Получить компанию по ID",
            description =
                    "Возвращает детальную информацию о компании по её уникальному идентификатору.")

    @ApiResponse(responseCode = "200", description = "Компания найдена и возвращена",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = CompanyDto.class)))
    @ApiResponse(responseCode = "400", description = "Некорректный ID")
    @ApiResponse(responseCode = "404", description = "Компания с указанным ID не найдена")

    public CompanyDto getCompany(
            @Parameter(description = "ID компании", required = true, example = "1")
            @PathVariable @Positive(message = "ID must be positive") Long id) {
        return companyService.getCompanyById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Создать новую компанию",
            description = "Создает новую запись о компании на основе переданных данных.")

    @ApiResponse(responseCode = "201", description = "Компания успешно создана",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = CompanyDto.class)))
    @ApiResponse(responseCode = "400", description = "Некорректные данные для создания компании")

    public CompanyDto createCompany(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Данные для создания новой компании", required = true,
                    content = @Content(schema = @Schema(implementation = CompanyDto.class)))
            @Valid @RequestBody CompanyDto companyDto) {
        return companyService.createCompany(companyDto);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Обновить существующую компанию",
            description = "Обновляет информацию о существующей компании по её ID.")

    @ApiResponse(responseCode = "200", description = "Компания успешно обновлена",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = CompanyDto.class)))
    @ApiResponse(responseCode = "400", description = "Некорректные данные или ID")
    @ApiResponse(responseCode = "404", description = "Компания с указанным ID не найдена")

    public CompanyDto updateCompany(
            @Parameter(description = "ID компании для обновления", required = true, example = "1")
            @PathVariable @Positive(message = "ID must be positive") Long id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Обновленные данные компании", required = true,
                    content = @Content(schema = @Schema(implementation = CompanyDto.class)))
            @Valid @RequestBody CompanyDto companyDto) {
        return companyService.updateCompany(id, companyDto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Удалить компанию",
            description = "Удаляет компанию по её уникальному идентификатору.")

    @ApiResponse(responseCode = "204", description = "Компания успешно удалена")
    @ApiResponse(responseCode = "400", description = "Некорректный ID")
    @ApiResponse(responseCode = "404", description = "Компания с указанным ID не найдена")

    public void deleteCompany(
            @Parameter(description = "ID компании для удаления", required = true, example = "1")
            @PathVariable @Positive(message = "ID must be positive") Long id) {
        companyService.deleteCompany(id);
    }

    @GetMapping("/{companyId}/employees")
    @Operation(summary = "Получить сотрудников компании по отделу",
            description = "Возвращает список сотрудников указанной компании, "
                    + "работающих в указанном отделе.")

    @ApiResponse(responseCode = "200", description = "Список сотрудников успешно получен",
            content = @Content(mediaType = "application/json",
                    array = @ArraySchema(schema = @Schema(implementation = EmployeeDto.class))))
    @ApiResponse(responseCode = "400", description = "Некорректный ID или название отдела")
    @ApiResponse(responseCode = "404", description = "Компания или отдел не найдены")

    public List<EmployeeDto> getEmployeesByDepartment(
            @Parameter(description = "ID компании", required = true, example = "1")
            @PathVariable @Positive(message = "ID must be positive") Long companyId,
            @Parameter(description = "Название отдела", required = true, example = "Разработка")
            @RequestParam @NotBlank(message =
                    "Department name cannot be empty") String departmentName) {
        return companyService.findEmployeesByDepartment(companyId, departmentName);
    }

    @GetMapping("/high-salary")
    @Operation(summary = "Найти компании с сотрудниками с высокой зарплатой",
            description = "Возвращает список компаний, в которых есть хотя бы "
                    + "один сотрудник с зарплатой, равной или превышающей указанное значение.")

    @ApiResponse(responseCode = "200", description = "Список компаний успешно получен",
            content = @Content(mediaType = "application/json",
                    array = @ArraySchema(schema = @Schema(implementation = CompanyDto.class))))
    @ApiResponse(responseCode = "400", description = "Некорректное значение зарплаты")

    public List<CompanyDto> getCompaniesWithHighSalaryEmployees(
            @Parameter(description = "Минимальный порог зарплаты",
                    required = true, example = "5000.00")
            @RequestParam @NotNull(message = "Salary cannot be null")
            @Positive(message = "Salary must be positive") BigDecimal salary) {
        return companyService.findCompaniesWithHighSalaryEmployeesNative(salary);
    }
}