package by.koronatech.office.core.service.impl;

import by.koronatech.office.api.dto.CompanyDto;
import by.koronatech.office.core.mapper.CompanyMapper;
import by.koronatech.office.core.model.Company;
import by.koronatech.office.core.repository.CompanyRepository;
import by.koronatech.office.core.service.CompanyService;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class CompanyServiceImpl implements CompanyService  {

    private final CompanyRepository companyRepository;
    private final CompanyMapper companyMapper;

    @Override
    public List<CompanyDto> getAllCompanies() {
        return companyMapper
                .toDtos(companyRepository.findAll());
    }

    @Override
    public CompanyDto getCompanyById(Long id) {
        Company company = companyRepository.findById(id).orElseThrow(() -> new RuntimeException("Company not found by id" + id));
        return companyMapper.toDto(company);
    }

    @Override
    public CompanyDto createCompany(CompanyDto companyDto) {
        Company company = companyMapper.toEntity(companyDto);
        Company savedCompany = companyRepository.save(company);
        return companyMapper.toDto(savedCompany);
    }

    @Override
    public CompanyDto updateCompany(Long id, CompanyDto companyDto) {
        Company existingCompany = companyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Company not found by id" + id));
        Company updatedCompany = companyMapper.toEntity(companyDto);
        updatedCompany.setId(existingCompany.getId());
        Company savedCompany = companyRepository.save(updatedCompany);
        return companyMapper.toDto(savedCompany);
    }

    @Override
    public void deleteCompany(Long id) {
        companyRepository.deleteById(id);
    }
}
