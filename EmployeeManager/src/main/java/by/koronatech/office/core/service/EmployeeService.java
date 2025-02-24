package by.koronatech.office.core.service;

import by.koronatech.office.api.dto.CreateEmployeeDto;
import by.koronatech.office.api.dto.EmployeeDto;
import by.koronatech.office.core.mapper.CreateEmployeeMapper;
import by.koronatech.office.core.mapper.EmployeeMapper;
import by.koronatech.office.core.repository.DepartmentRepository;
import by.koronatech.office.core.repository.EmployeeRepository;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class EmployeeService implements BaseEmployeeService {

    private final EmployeeRepository employeeRepository;
    private final DepartmentRepository departmentRepository;

    private final EmployeeMapper employeeMapper;
    private final CreateEmployeeMapper createEmployeeMapper;

    @Override
    public EmployeeDto createEmployee(CreateEmployeeDto createEmployeeDto) {
        employeeRepository
                .addEmployee(createEmployeeMapper
                        .toEntity(createEmployeeDto, departmentRepository));
        return employeeMapper
                .toDto(createEmployeeMapper
                        .toEntity(createEmployeeDto, departmentRepository));
    }

    @Override
    public List<EmployeeDto> findAllEmployeesByDepartment(String department) {
        return employeeMapper
                .toDtos(employeeRepository.getEmployees().values().stream().toList())
                .stream()
                .filter(employee -> employee.getDepartmentName().equals(department))
                .toList();
    }

    @Override
    public EmployeeDto setManagerEmployee(int employeeId) {
        employeeRepository.findEmployeeById(employeeId).setManager(true);
        return employeeMapper.toDto(employeeRepository.findEmployeeById(employeeId));
    }

    @Override
    public EmployeeDto updateEmployee(int id, EmployeeDto employeeDto) {
        return employeeMapper
                .toDto(employeeMapper
                        .merge(employeeRepository
                                .findEmployeeById(id), employeeDto));
    }

    @Override
    public void deleteEmployee(int employeeId) {
        employeeRepository.deleteEmployee(employeeId);
    }

    @Override
    public EmployeeDto findEmployeeById(int employeeId) {
        return employeeMapper.toDto(employeeRepository.findEmployeeById(employeeId));
    }
}
