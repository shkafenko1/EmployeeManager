package by.koronatech.office.core.mapper;

import by.koronatech.office.api.dto.DepartmentReturnDto;
import by.koronatech.office.core.model.Department;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = BaseMapper.class, componentModel = "spring")
public interface DepartmentReturnMapper extends BaseMapper<Department, DepartmentReturnDto> {

    // Entity to DTO: extract the company name and include the department ID.
    @Override
    @Mapping(source = "company.name", target = "company")
    DepartmentReturnDto toDto(Department department);

    // Explicitly disable the toEntity mapping, as it's not needed for GET requests.
    @Override
    default Department toEntity(DepartmentReturnDto dto) {
        throw new UnsupportedOperationException("toEntity is not supported for DepartmentReturnMapper.");
    }

    // Explicitly disable the merge method, as it's not needed for GET requests.
    @Override
    default Department merge(Department entity, DepartmentReturnDto dto) {
        throw new UnsupportedOperationException("Merge is not supported for DepartmentReturnMapper.");
    }
}