package by.koronatech.office.core.mapper;

import by.koronatech.office.api.dto.EmployeeDto;
import by.koronatech.office.core.Employee;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = BaseMapper.class)
public interface EmployeeMapper extends BaseMapper<Employee, EmployeeDto> {
    @Mapping(source = "department.name", target = "departmentName")
    EmployeeDto toDto(Employee employee);
}
