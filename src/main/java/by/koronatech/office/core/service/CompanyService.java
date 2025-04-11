package by.koronatech.office.core.service;

import by.koronatech.office.api.dto.CompanyDto;
import java.util.List;

public interface CompanyService {
    List<CompanyDto> getAllCompanies();

    CompanyDto getCompanyById(Long id);

    CompanyDto createCompany(CompanyDto companyDto);

    CompanyDto updateCompany(Long id, CompanyDto updatedCompanyDto);

    void deleteCompany(Long companyId);
}
