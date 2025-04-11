package by.koronatech.office.core.mapper;

import by.koronatech.office.api.dto.CreateEmployeeDto;
import by.koronatech.office.core.model.Employee;
import by.koronatech.office.core.repository.DepartmentRepository;
import java.util.HashSet;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CreateEmployeeMapper {
    @Mapping(target = "employeeDepartments", ignore = true)
    Employee toEntity(CreateEmployeeDto dto);

    default Employee toEntityWithDepartments(CreateEmployeeDto dto,
                                             DepartmentRepository departmentRepo) {
        Employee employee = toEntity(dto);
        employee.setEmployeeDepartments(new HashSet<>());  // Explicit initialization
        return employee;
    }
}