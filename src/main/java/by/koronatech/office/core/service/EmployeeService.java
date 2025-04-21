package by.koronatech.office.core.service;

import by.koronatech.office.api.dto.CreateEmployeeDto;
import by.koronatech.office.api.dto.EmployeeDto;
import by.koronatech.office.api.dto.UpdateDto;

import java.util.List;
import java.util.Map;

public interface EmployeeService {

    EmployeeDto createEmployee(CreateEmployeeDto employeeDto);

    List<EmployeeDto> getAllEmployees();

    public Map<String, Object> bulkCreateEmployees(List<CreateEmployeeDto> employeeDtos);

    List<EmployeeDto> findAllEmployeesByDepartment(String department);

    EmployeeDto updateEmployee(Long id, UpdateDto employeeDto);

    EmployeeDto findEmployeeById(Long employeeId);

    void deleteEmployee(Long employeeId);
}
