package by.koronatech.office.core.service.impl;

import by.koronatech.office.api.dto.CreateEmployeeDto;
import by.koronatech.office.api.dto.EmployeeDto;
import by.koronatech.office.api.dto.UpdateDto;
import by.koronatech.office.core.exceptions.EntityNotFound;
import by.koronatech.office.core.model.Department;
import by.koronatech.office.core.model.Employee;
import by.koronatech.office.core.repository.DepartmentRepository;
import by.koronatech.office.core.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
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

    private final EmployeeRepository employeeRepository;
    private final DepartmentRepository departmentRepository;
    private final Validator validator;

    public List<EmployeeDto> getAllEmployees() {
        return employeeRepository.findAll().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public EmployeeDto createEmployee(CreateEmployeeDto employeeDto) {
        Employee employee = toEntity(employeeDto);
        return toDto(employeeRepository.save(employee));
    }

    public Map<String, Object> bulkCreateEmployees(List<CreateEmployeeDto> employeeDtos) {
        Map<String, Object> result = new HashMap<>();
        List<EmployeeDto> createdEmployees = new ArrayList<>();
        Map<String, Map<String, String>> errors = new HashMap<>();

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
                    } else {
                        try {
                            Employee employee = toEntity(dto);
                            createdEmployees.add(toDto(employeeRepository.save(employee)));
                        } catch (EntityNotFound e) {
                            errors.put(key, Map.of("general", e.getMessage() != null ? e.getMessage() : "Entity not found"));
                        } catch (Exception e) {
                            errors.put(key, Map.of("general", "Failed to create employee: " + (e.getMessage() != null ? e.getMessage() : "Unknown error")));
                        }
                    }
                });

        result.put("created", createdEmployees);
        result.put("errors", errors);
        return result;
    }

    public List<EmployeeDto> findAllEmployeesByDepartment(String department) {
        Page<Employee> page = employeeRepository.findByEmployeeDepartmentsDepartmentName(
                department, PageRequest.of(0, Integer.MAX_VALUE));
        return page.getContent().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public EmployeeDto updateEmployee(Long id, UpdateDto employeeDto) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFound("Employee with ID " + id + " not found"));
        updateEntity(employee, employeeDto);
        return toDto(employeeRepository.save(employee));
    }

    public void deleteEmployee(Long id) {
        if (!employeeRepository.existsById(id)) {
            throw new EntityNotFound("Employee with ID " + id + " not found");
        }
        employeeRepository.deleteById(id);
    }

    public EmployeeDto findEmployeeById(Long id) {
        return employeeRepository.findById(id)
                .map(this::toDto)
                .orElseThrow(() -> new EntityNotFound("Employee with ID " + id + " not found"));
    }

    private Employee toEntity(CreateEmployeeDto dto) {
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