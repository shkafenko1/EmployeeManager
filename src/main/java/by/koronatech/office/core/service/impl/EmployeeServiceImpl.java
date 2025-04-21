package by.koronatech.office.core.service.impl;

import by.koronatech.office.api.dto.CreateEmployeeDto;
import by.koronatech.office.api.dto.EmployeeDto;
import by.koronatech.office.api.dto.UpdateDto;
import by.koronatech.office.core.exceptions.EntityNotFound;
import by.koronatech.office.core.exceptions.HttpStatusException;
import by.koronatech.office.core.model.Department;
import by.koronatech.office.core.model.Employee;
import by.koronatech.office.core.repository.DepartmentRepository;
import by.koronatech.office.core.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;
import org.springframework.validation.Validator;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EmployeeServiceImpl {
    private static final Logger logger = LoggerFactory.getLogger(EmployeeServiceImpl.class);

    private final EmployeeRepository employeeRepository;
    private final DepartmentRepository departmentRepository;
    private final Validator validator;

    public List<EmployeeDto> getAllEmployees() {
        try {
            List<EmployeeDto> employees = employeeRepository.findAll().stream()
                    .map(this::toDto)
                    .collect(Collectors.toList());
            logger.info("Retrieved all employees: {} found", employees.size());
            return employees;
        } catch (Exception e) {
            logger.error("Failed to retrieve employees (HTTP 500): {}", e.getMessage(), new HttpStatusException(500));
            throw new HttpStatusException(500);
        }
    }

    public EmployeeDto createEmployee(CreateEmployeeDto employeeDto) {
        if (employeeDto == null) {
            logger.error("Create employee failed (HTTP 400): DTO is null", new HttpStatusException(400));
            throw new HttpStatusException(400);
        }
        Errors validationErrors = new BeanPropertyBindingResult(employeeDto, "employeeDto");
        validator.validate(employeeDto, validationErrors);
        if (validationErrors.hasErrors()) {
            Map<String, String> errorMap = validationErrors.getFieldErrors().stream()
                    .collect(Collectors.toMap(
                            FieldError::getField,
                            fieldError -> fieldError.getDefaultMessage() != null ? fieldError.getDefaultMessage() : "Validation error",
                            (existing, replacement) -> existing
                    ));
            logger.error("Validation error for employee [{}] (HTTP 400): {}", employeeDto.getName(), errorMap, new HttpStatusException(400));
            throw new HttpStatusException(400);
        }
        try {
            Employee employee = toEntity(employeeDto);
            EmployeeDto result = toDto(employeeRepository.save(employee));
            logger.info("Created employee [{}]", result.getName());
            return result;
        } catch (EntityNotFound e) {
            logger.error("Entity not found for employee [{}] (HTTP 404): {}", employeeDto.getName(), e.getMessage(), new HttpStatusException(404));
            throw new HttpStatusException(404);
        } catch (Exception e) {
            logger.error("Failed to create employee [{}] (HTTP 500): {}", employeeDto.getName(), e.getMessage(), new HttpStatusException(500));
            throw new HttpStatusException(500);
        }
    }

    public Map<String, Object> bulkCreateEmployees(List<CreateEmployeeDto> employeeDtos) {
        Map<String, Object> result = new HashMap<>();
        List<EmployeeDto> createdEmployees = new ArrayList<>();
        Map<String, Map<String, String>> errors = new HashMap<>();

        if (employeeDtos == null) {
            logger.error("Bulk create employees failed (HTTP 400): DTO list is null", new HttpStatusException(400));
            result.put("errors", Map.of("general", "DTO list is null"));
            return result;
        }

        employeeDtos.stream()
                .map(dto -> {
                    Errors validationErrors = new BeanPropertyBindingResult(dto, "employeeDto");
                    validator.validate(dto, validationErrors);
                    return Map.entry(dto, validationErrors);
                })
                .forEach(entry -> {
                    CreateEmployeeDto dto = entry.getKey();
                    Errors validationErrors = entry.getValue();
                    String key = dto.getName() != null && !dto.getName().isEmpty() ? dto.getName() : "unknown_" + UUID.randomUUID().toString();

                    if (validationErrors.hasErrors()) {
                        Map<String, String> errorMap = validationErrors.getFieldErrors().stream()
                                .collect(Collectors.toMap(
                                        FieldError::getField,
                                        fieldError -> fieldError.getDefaultMessage() != null ? fieldError.getDefaultMessage() : "Validation error",
                                        (existing, replacement) -> existing
                                ));
                        errors.put(key, errorMap);
                        logger.error("Validation error for employee [{}] (HTTP 400): {}", key, errorMap, new HttpStatusException(400));
                    } else {
                        try {
                            Employee employee = toEntity(dto);
                            EmployeeDto saved = toDto(employeeRepository.save(employee));
                            createdEmployees.add(saved);
                            logger.info("Created employee [{}] in bulk operation", key);
                        } catch (EntityNotFound e) {
                            errors.put(key, Map.of("general", e.getMessage() != null ? e.getMessage() : "Entity not found"));
                            logger.error("Entity not found for employee [{}] (HTTP 404): {}", key, e.getMessage(), new HttpStatusException(404));
                        } catch (Exception e) {
                            errors.put(key, Map.of("general", "Failed to create employee: " + (e.getMessage() != null ? e.getMessage() : "Unknown error")));
                            logger.error("Failed to create employee [{}] (HTTP 500): {}", key, e.getMessage(), new HttpStatusException(500));
                        }
                    }
                });

        result.put("created", createdEmployees);
        result.put("errors", errors);
        logger.info("Bulk create completed: {} created, {} errors", createdEmployees.size(), errors.size());
        return result;
    }

    public List<EmployeeDto> findAllEmployeesByDepartment(String department) {
        if (department == null || department.isEmpty()) {
            logger.error("Find employees by department failed (HTTP 400): Department name is null or empty", new HttpStatusException(400));
            throw new HttpStatusException(400);
        }
        try {
            Page<Employee> page = employeeRepository.findByEmployeeDepartmentsDepartmentName(
                    department, PageRequest.of(0, Integer.MAX_VALUE));
            List<EmployeeDto> employees = page.getContent().stream()
                    .map(this::toDto)
                    .collect(Collectors.toList());
            logger.info("Retrieved employees for department [{}]: {} found", department, employees.size());
            return employees;
        } catch (Exception e) {
            logger.error("Failed to retrieve employees for department [{}] (HTTP 500): {}", department, e.getMessage(), new HttpStatusException(500));
            throw new HttpStatusException(500);
        }
    }

    public EmployeeDto updateEmployee(Long id, UpdateDto employeeDto) {
        if (id == null || employeeDto == null) {
            logger.error("Update employee failed (HTTP 400): ID or DTO is null", new HttpStatusException(400));
            throw new HttpStatusException(400);
        }
        Errors validationErrors = new BeanPropertyBindingResult(employeeDto, "updateDto");
        validator.validate(employeeDto, validationErrors);
        if (validationErrors.hasErrors()) {
            Map<String, String> errorMap = validationErrors.getFieldErrors().stream()
                    .collect(Collectors.toMap(
                            FieldError::getField,
                            fieldError -> fieldError.getDefaultMessage() != null ? fieldError.getDefaultMessage() : "Validation error",
                            (existing, replacement) -> existing
                    ));
            logger.error("Validation error for employee ID [{}] (HTTP 400): {}", id, errorMap, new HttpStatusException(400));
            throw new HttpStatusException(400);
        }
        try {
            Employee employee = employeeRepository.findById(id)
                    .orElseThrow(() -> new EntityNotFound("Employee with ID " + id + " not found"));
            updateEntity(employee, employeeDto);
            EmployeeDto result = toDto(employeeRepository.save(employee));
            logger.info("Updated employee ID [{}]", id);
            return result;
        } catch (EntityNotFound e) {
            logger.error("Entity not found for employee ID [{}] (HTTP 404): {}", id, e.getMessage(), new HttpStatusException(404));
            throw new HttpStatusException(404);
        } catch (Exception e) {
            logger.error("Failed to update employee ID [{}] (HTTP 500): {}", id, e.getMessage(), new HttpStatusException(500));
            throw new HttpStatusException(500);
        }
    }

    public void deleteEmployee(Long id) {
        if (id == null) {
            logger.error("Delete employee failed (HTTP 400): ID is null", new HttpStatusException(400));
            throw new HttpStatusException(400);
        }
        try {
            if (!employeeRepository.existsById(id)) {
                logger.error("Employee not found for ID [{}] (HTTP 404)", id, new HttpStatusException(404));
                throw new HttpStatusException(404);
            }
            employeeRepository.deleteById(id);
            logger.info("Deleted employee ID [{}]", id);
        } catch (HttpStatusException e) {
            throw e; // Re-throw EntityNotFound as HttpStatusException
        } catch (Exception e) {
            logger.error("Failed to delete employee ID [{}] (HTTP 500): {}", id, e.getMessage(), new HttpStatusException(500));
            throw new HttpStatusException(500);
        }
    }

    public EmployeeDto findEmployeeById(Long id) {
        if (id == null) {
            logger.error("Find employee failed (HTTP 400): ID is null", new HttpStatusException(400));
            throw new HttpStatusException(400);
        }
        try {
            EmployeeDto result = employeeRepository.findById(id)
                    .map(this::toDto)
                    .orElseThrow(() -> new EntityNotFound("Employee with ID " + id + " not found"));
            logger.info("Retrieved employee ID [{}]", id);
            return result;
        } catch (EntityNotFound e) {
            logger.error("Employee not found for ID [{}] (HTTP 404): {}", id, e.getMessage(), new HttpStatusException(404));
            throw new HttpStatusException(404);
        } catch (Exception e) {
            logger.error("Failed to retrieve employee ID [{}] (HTTP 500): {}", id, e.getMessage(), new HttpStatusException(500));
            throw new HttpStatusException(500);
        }
    }

    private Employee toEntity(CreateEmployeeDto dto) {
        if (dto == null) {
            logger.error("Convert to entity failed (HTTP 400): DTO is null", new HttpStatusException(400));
            throw new HttpStatusException(400);
        }
        Set<Department> departments = Optional.ofNullable(dto.getDepartmentNames())
                .orElse(Collections.emptyList())
                .stream()
                .map(name -> departmentRepository.findByName(name)
                        .orElseThrow(() -> new EntityNotFound("Department " + name + " not found")))
                .collect(Collectors.toSet());

        Employee employee = Employee.builder()
                .name(dto.getName())
                .salary(dto.getSalary())
                .manager(dto.isManager())
                .build();
        employee.updateDepartments(departments);
        return employee;
    }

    private void updateEntity(Employee employee, UpdateDto dto) {
        employee.setName(dto.getName());
        employee.setSalary(dto.getSalary());
        employee.setManager(dto.isManager());
    }

    private EmployeeDto toDto(Employee employee) {
        return EmployeeDto.builder()
                .id(employee.getId())
                .name(employee.getName())
                .salary(employee.getSalary())
                .departmentNames(employee.getEmployeeDepartments().stream()
                        .map(ed -> ed.getDepartment().getName())
                        .collect(Collectors.toList()))
                .manager(employee.isManager())
                .build();
    }
}

