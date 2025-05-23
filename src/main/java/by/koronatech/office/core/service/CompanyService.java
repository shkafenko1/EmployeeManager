package by.koronatech.office.core.service;

import by.koronatech.office.api.dto.CompanyDto;
import by.koronatech.office.api.dto.CompanyReturnDto;
import by.koronatech.office.api.dto.EmployeeDto;
import java.math.BigDecimal;
import java.util.List;

public interface CompanyService {
    List<CompanyReturnDto> getAllCompanies();

    CompanyReturnDto getCompanyById(Long id);

    CompanyDto createCompany(CompanyDto companyDto);

    CompanyDto updateCompany(Long id, CompanyDto updatedCompanyDto);

    void deleteCompany(Long companyId);

    List<EmployeeDto> findEmployeesByDepartment(Long companyId, String departmentName);

    List<CompanyReturnDto> findCompaniesWithHighSalaryEmployeesNative(BigDecimal salary);
}
