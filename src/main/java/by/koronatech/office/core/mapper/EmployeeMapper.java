package by.koronatech.office.core.mapper;

import by.koronatech.office.api.dto.EmployeeDto;
import by.koronatech.office.core.model.Department;
import by.koronatech.office.core.model.Employee;
import java.util.Set;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(config = BaseMapper.class)
public interface EmployeeMapper extends BaseMapper<Employee, EmployeeDto> {

    @Mapping(source = "departments", target = "departmentName", qualifiedByName = "mapDepartmentSet")
    EmployeeDto toDto(Employee employee);

    @Named("mapDepartmentSet")
    default String mapDepartmentSet(Set<Department> departments) {
        if (departments == null || departments.isEmpty()) {
            return null;
        }
        // Return the name of the first department. Change logic if needed.
        return departments.iterator().next().getName();
    }
}