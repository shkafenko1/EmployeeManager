package by.koronatech.office.core.mapper;

import by.koronatech.office.api.dto.CompanyReturnDto;
import by.koronatech.office.core.model.Company;
import org.mapstruct.Mapper;

@Mapper(config = BaseMapper.class)
public interface CompanyReturnMapper extends BaseMapper<Company, CompanyReturnDto> {
}
