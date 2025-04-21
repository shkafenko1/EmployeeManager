package by.koronatech.office.core.mapper;

import by.koronatech.office.api.dto.CreateEmployeeDto;
import by.koronatech.office.api.dto.EmployeeDto;
import by.koronatech.office.core.model.Employee;
import by.koronatech.office.core.model.EmployeeDepartment;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface EmployeeMapper {

    @Mapping(target = "departmentNames",
            expression = "java(mapDepartments(employee.getEmployeeDepartments()))")
    EmployeeDto toDto(Employee employee);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "employeeDepartments", ignore = true)
    @Mapping(target = "name", source = "name", defaultValue = "")
    @Mapping(target = "salary", source = "salary", defaultValue = "0")
    Employee toEntity(CreateEmployeeDto dto);

    default List<String> mapDepartments(Set<EmployeeDepartment> employeeDepartments) {
        if (employeeDepartments == null) {
            return Collections.emptyList();
        }
        return employeeDepartments.stream()
                .map(ed -> ed.getDepartment().getName())
                .toList();
    }
}