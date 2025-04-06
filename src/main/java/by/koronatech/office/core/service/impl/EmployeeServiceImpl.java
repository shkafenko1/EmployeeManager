package by.koronatech.office.core.service.impl;

import by.koronatech.office.api.dto.CreateEmployeeDto;
import by.koronatech.office.api.dto.EmployeeDto;
import by.koronatech.office.core.exceptions.EntityNotFound;
import by.koronatech.office.core.mapper.CreateEmployeeMapper;
import by.koronatech.office.core.mapper.EmployeeMapper;
import by.koronatech.office.core.model.Employee;
import by.koronatech.office.core.repository.DepartmentRepository;
import by.koronatech.office.core.repository.EmployeeRepository;
import by.koronatech.office.core.service.EmployeeService;
import jakarta.transaction.Transactional;
import java.util.List;
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

    @Override
    public EmployeeDto createEmployee(CreateEmployeeDto createEmployeeDto) {
        employeeRepository
                .save(createEmployeeMapper
                        .toEntity(createEmployeeDto, departmentRepository));
        return employeeMapper
                .toDto(createEmployeeMapper
                        .toEntity(createEmployeeDto, departmentRepository));
    }

    @Override
    public List<EmployeeDto> findAllEmployeesByDepartment(String department) {
        // Replace the stream filter with a proper repository call
        // (assuming department parameter is the department ID)
        long departmentId = Long.parseLong(department); // Ensure department is passed as ID
        return employeeMapper.toDtos(
                employeeRepository.findEmployeesByDepartments_Id(departmentId, Pageable.unpaged()).getContent()
        );
    }

    @Override
    @Transactional
    public EmployeeDto setManagerEmployee(Long employeeId) {
        employeeRepository.findById(employeeId)
                .orElseThrow(
                        () -> new EntityNotFound(String.valueOf(employeeId))
                )
                .setManager(true);
        return employeeMapper
                .toDto(employeeRepository
                        .findById(employeeId)
                        .orElseThrow(
                                () -> new EntityNotFound(String.valueOf(employeeId))
                        ));
    }

    @Override
    @Transactional
    public EmployeeDto updateEmployee(Long id, EmployeeDto employeeDto) {
        Employee employee = employeeRepository
                .findById(id)
                .orElseThrow(
                        () -> new EntityNotFound(
                                "Employee not found with id: " + id
                        )
                );
        return employeeMapper
                .toDto(employeeMapper
                        .merge(employee, employeeDto));
    }

    @Override
    public void deleteEmployee(Long employeeId) {
        employeeRepository.deleteById(employeeId);
    }

    @Override
    public EmployeeDto findEmployeeById(Long employeeId) {
        if (employeeRepository.existsById(employeeId)) {
            return employeeMapper
                    .toDto(employeeRepository
                            .findById(employeeId)
                            .orElseThrow(
                                    () -> new EntityNotFound(
                                            "Employee not found with id: " + employeeId
                                    )
                            ));
        } else {
            throw new EntityNotFound("Employee not found by id" + employeeId);
        }
    }

    @Override
    public List<EmployeeDto> getAllEmployees() {
        return employeeMapper.toDtos(employeeRepository.findAll());
    }
}
