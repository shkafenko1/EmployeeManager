package by.koronatech.office.core.mapper;

import by.koronatech.office.api.dto.CreateEmployeeDto;
import by.koronatech.office.core.exceptions.EntityNotFound;
import by.koronatech.office.core.model.Department;
import by.koronatech.office.core.model.Employee;
import by.koronatech.office.core.repository.DepartmentRepository;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.Collections;
import java.util.Set;

@Mapper(config = BaseMapper.class)
public interface CreateEmployeeMapper {

    // Convert Employee to CreateEmployeeDto
    @Mapping(source = "departments", target = "departmentName", qualifiedByName = "mapDepartmentSet")
    CreateEmployeeDto toDto(Employee employee);

    // Convert CreateEmployeeDto to Employee
    // Here, we use a custom method to convert a String to a Set<Department>
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "departments", source = "departmentName", qualifiedByName = "mapStringToDepartmentSet")
    Employee toEntity(CreateEmployeeDto createEmployeeDto,
                      @Context DepartmentRepository departmentRepository);

    // Custom conversion: from Set<Department> to String (e.g. first department’s name)
    @Named("mapDepartmentSet")
    default String mapDepartmentSet(Set<Department> departments) {
        if (departments == null || departments.isEmpty()) {
            return null;
        }
        return departments.iterator().next().getName();
    }

    // Custom conversion: from String (department name) to Set<Department>
    @Named("mapStringToDepartmentSet")
    default Set<Department> mapStringToDepartmentSet(String departmentName,
                                                     @Context DepartmentRepository departmentRepository) {
        if (departmentName == null) {
            return Collections.emptySet();
        }
        Department department = departmentRepository.findByName(departmentName)
                .orElseThrow(() -> new EntityNotFound("Not found dep with name: " + departmentName));
        return Collections.singleton(department);
    }

    // Optionally, if BaseMapper declares a single-argument toEntity method,
    // override it to prevent its accidental use.
    default Employee toEntity(CreateEmployeeDto createEmployeeDto) {
        throw new UnsupportedOperationException("Use toEntity(dto, departmentRepository) instead");
    }
}