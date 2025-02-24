package by.koronatech.office.core.mapper;

import by.koronatech.office.api.dto.CreateEmployeeDto;
import by.koronatech.office.core.Department;
import by.koronatech.office.core.Employee;
import by.koronatech.office.core.repository.DepartmentRepository;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(config = BaseMapper.class)
public interface CreateEmployeeMapper extends BaseMapper<Employee, CreateEmployeeDto> {
    @Mapping(source = "department.name", target = "departmentName")
    CreateEmployeeDto toDto(Employee employee);

    @Mapping(target = "department",
            source = "departmentName",
            qualifiedByName = "mapStringToDepartment")
    Employee toEntity(CreateEmployeeDto createEmployeeDto,
                      @Context DepartmentRepository departmentRepository);

    @Named("mapStringToDepartment")
    default Department mapStringToDepartment(String department,
                                             @Context DepartmentRepository departmentRep) {
        if (department == null) {
            return null;
        }
        return departmentRep.findDepartmentByName(department);
    }
}
