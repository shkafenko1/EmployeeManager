package by.koronatech.office.core.mapper;

import by.koronatech.office.api.dto.DepartmentDto;
import by.koronatech.office.core.exceptions.EntityNotFound;
import by.koronatech.office.core.model.Company;
import by.koronatech.office.core.model.Department;
import by.koronatech.office.core.repository.CompanyRepository;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(config = BaseMapper.class, componentModel = "spring")
public interface DepartmentMapper extends BaseMapper<Department, DepartmentDto> {

    // Entity to DTO: extract the company name.
    @Override
    @Mapping(source = "company.name", target = "company")
    DepartmentDto toDto(Department department);

    // DTO to Entity mapping: uses a context parameter for company lookup.
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "company", source = "company", qualifiedByName = "mapCompanyNameToCompany")
    Department toEntity(DepartmentDto departmentDto, @Context CompanyRepository companyRepository);

    // Override the single-argument mapping method to avoid its accidental use.
    @Override
    default Department toEntity(DepartmentDto dto) {
        throw new UnsupportedOperationException("Use toEntity(dto, companyRepository) instead");
    }

    // Override the merge method to avoid MapStruct attempting to map 'company' automatically.
    @Override
    default Department merge(Department entity, DepartmentDto dto) {
        throw new UnsupportedOperationException("Merge is not supported.");
    }

    @Named("mapCompanyNameToCompany")
    default Company mapCompanyNameToCompany(String companyName,
                                            @Context CompanyRepository companyRepository) {
        return companyRepository.findByName(companyName)
                .orElseThrow(()
                        -> new EntityNotFound("Company not found with name: " + companyName));
    }
}