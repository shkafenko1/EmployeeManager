package by.koronatech.office.core.service;

import by.koronatech.office.api.dto.CreateEmployeeDto;
import by.koronatech.office.api.dto.EmployeeDto;
import java.util.List;

public interface BaseEmployeeService {

    EmployeeDto createEmployee(CreateEmployeeDto employeeDto);

    List<EmployeeDto> findAllEmployeesByDepartment(String department);

    EmployeeDto setManagerEmployee(int employeeId);

    EmployeeDto updateEmployee(int id, EmployeeDto employeeDto);

    EmployeeDto findEmployeeById(int employeeId);

    void deleteEmployee(int employeeId);
}
