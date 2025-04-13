package by.koronatech.office.core.service.impl;

import by.koronatech.office.api.dto.CreateEmployeeDto;
import by.koronatech.office.api.dto.EmployeeDto;
import by.koronatech.office.api.dto.UpdateDto;
import by.koronatech.office.core.cache.Cache;
import by.koronatech.office.core.exceptions.EntityNotFound;
import by.koronatech.office.core.mapper.CreateEmployeeMapper;
import by.koronatech.office.core.mapper.EmployeeMapper;
import by.koronatech.office.core.model.Department;
import by.koronatech.office.core.model.Employee;
import by.koronatech.office.core.repository.DepartmentRepository;
import by.koronatech.office.core.repository.EmployeeRepository;
import by.koronatech.office.core.service.EmployeeService;
import jakarta.transaction.Transactional;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final DepartmentRepository departmentRepository;
    private final EmployeeMapper employeeMapper;
    private final CreateEmployeeMapper createEmployeeMapper;
    private final Cache cache;

    @Override
    @Transactional
    public EmployeeDto createEmployee(CreateEmployeeDto createEmployeeDto) {
        Employee employee = createEmployeeMapper.toEntityWithDepartments(
                createEmployeeDto,
                departmentRepository
        );

        Set<Department> departments = resolveDepartments(
                createEmployeeDto.getDepartmentNames() != null
                        ? createEmployeeDto.getDepartmentNames()
                        : Collections.emptyList()
        );

        employee.updateDepartments(departments);

        Employee savedEmployee = employeeRepository.save(employee);
        return employeeMapper.toDto(savedEmployee);
    }

    @Override
    public List<EmployeeDto> findAllEmployeesByDepartment(String department) {
        return employeeRepository.findByEmployeeDepartmentsDepartmentName(
                        department, Pageable.unpaged())
                .getContent()
                .stream()
                .map(employeeMapper::toDto)
                .toList();
    }

    @Override
    public List<EmployeeDto> getAllEmployees() {
        return employeeRepository.findAll()
                .stream()
                .map(employeeMapper::toDto)
                .toList();
    }

    @Override
    @Transactional
    public EmployeeDto updateEmployee(Long id, UpdateDto employeeDto) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Employee not found: " + id));
        employee.setName(employeeDto.getName());
        employee.setSalary(employeeDto.getSalary());
        Employee updatedEmployee = employeeRepository.save(employee);
        EmployeeDto result = employeeMapper.toDto(updatedEmployee);

        // Обновляем кэш
        cache.putEmployee(id, result);

        return result;
    }

    private Set<Department> resolveDepartments(List<String> departmentNames) {
        if (departmentNames.isEmpty()) {
            return new HashSet<>();
        }
        return departmentNames.stream()
                .map(name -> {
                    Department dept = departmentRepository.findByName(name)
                            .orElseThrow(() -> new EntityNotFound("Department not found: " + name));
                    if (dept.getCompany() == null) {
                        throw new IllegalStateException("Department "
                                + name + " has no associated Company");
                    }
                    return dept;
                })
                .collect(Collectors.toSet());
    }

    @Override
    public void deleteEmployee(Long employeeId) {
        employeeRepository.deleteById(employeeId);

        // Удаляем из кэша
        cache.removeEmployee(employeeId);
    }

    @Override
    @Transactional
    public EmployeeDto findEmployeeById(Long employeeId) {
        // Проверяем кэш
        EmployeeDto cachedEmployee = cache.getEmployee(employeeId);
        if (cachedEmployee != null) {
            return cachedEmployee;
        }

        // Если в кэше нет, ищем в БД
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new EntityNotFound("Employee not found with id: " + employeeId));
        EmployeeDto employeeDto = employeeMapper.toDto(employee);

        // Сохраняем в кэш
        cache.putEmployee(employeeId, employeeDto);

        return employeeDto;
    }
}