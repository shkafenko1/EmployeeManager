package by.koronatech.office.core.mapper;

import by.koronatech.office.api.dto.DepartmentDto;
import by.koronatech.office.core.model.Department;
import org.mapstruct.Mapper;

@Mapper(config = BaseMapper.class)
public interface DepartmentMapper extends BaseMapper<Department, DepartmentDto> {
}
