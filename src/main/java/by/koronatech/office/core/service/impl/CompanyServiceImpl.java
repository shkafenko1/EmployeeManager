package by.koronatech.office.core.service.impl;

import by.koronatech.office.api.dto.CompanyDto;
import by.koronatech.office.api.dto.EmployeeDto;
import by.koronatech.office.core.exceptions.EntityNotFound;
import by.koronatech.office.core.exceptions.HttpStatusException;
import by.koronatech.office.core.mapper.CompanyMapper;
import by.koronatech.office.core.model.Company;
import by.koronatech.office.core.model.Employee;
import by.koronatech.office.core.repository.CompanyRepository;
import by.koronatech.office.core.service.CompanyService;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@AllArgsConstructor
public class CompanyServiceImpl implements CompanyService {
    private static final Logger logger = LoggerFactory.getLogger(CompanyServiceImpl.class);

    private final CompanyRepository companyRepository;
    private final CompanyMapper companyMapper;

    @Override
    public List<CompanyDto> getAllCompanies() {
        try {
            List<CompanyDto> companies = companyMapper.toDtos(companyRepository.findAll());
            logger.info("Retrieved all companies: {} found", companies.size());
            return companies;
        } catch (Exception e) {
            logger.error("Failed to retrieve companies (HTTP 500): {}", e.getMessage(), new HttpStatusException(500));
            throw new HttpStatusException(500);
        }
    }

    @Override
    public CompanyDto getCompanyById(Long id) {
        if (id == null) {
            logger.error("Get company failed (HTTP 400): ID is null", new HttpStatusException(400));
            throw new HttpStatusException(400);
        }
        try {
            Company company = companyRepository.findById(id)
                    .orElseThrow(() -> new EntityNotFound("Company not found by id: " + id));
            CompanyDto result = companyMapper.toDto(company);
            logger.info("Retrieved company ID [{}]", id);
            return result;
        } catch (EntityNotFound e) {
            logger.error("Company not found for ID [{}] (HTTP 404): {}", id, e.getMessage(), new HttpStatusException(404));
            throw new HttpStatusException(404);
        } catch (Exception e) {
            logger.error("Failed to retrieve company ID [{}] (HTTP 500): {}", id, e.getMessage(), new HttpStatusException(500));
            throw new HttpStatusException(500);
        }
    }

    @Override
    public CompanyDto createCompany(CompanyDto companyDto) {
        if (companyDto == null) {
            logger.error("Create company failed (HTTP 400): DTO is null", new HttpStatusException(400));
            throw new HttpStatusException(400);
        }
        try {
            Company company = companyMapper.toEntity(companyDto);
            Company savedCompany = companyRepository.save(company);
            CompanyDto result = companyMapper.toDto(savedCompany);
            logger.info("Created company [{}]", result.getName());
            return result;
        } catch (Exception e) {
            logger.error("Failed to create company [{}] (HTTP 500): {}", companyDto.getName(), e.getMessage(), new HttpStatusException(500));
            throw new HttpStatusException(500);
        }
    }

    @Override
    public CompanyDto updateCompany(Long id, CompanyDto companyDto) {
        if (id == null || companyDto == null) {
            logger.error("Update company failed (HTTP 400): ID or DTO is null", new HttpStatusException(400));
            throw new HttpStatusException(400);
        }
        try {
            Company existingCompany = companyRepository.findById(id)
                    .orElseThrow(() -> new EntityNotFound("Company not found by id: " + id));
            Company updatedCompany = companyMapper.toEntity(companyDto);
            updatedCompany.setId(existingCompany.getId());
            Company savedCompany = companyRepository.save(updatedCompany);
            CompanyDto result = companyMapper.toDto(savedCompany);
            logger.info("Updated company ID [{}]", id);
            return result;
        } catch (EntityNotFound e) {
            logger.error("Company not found for ID [{}] (HTTP 404): {}", id, e.getMessage(), new HttpStatusException(404));
            throw new HttpStatusException(404);
        } catch (Exception e) {
            logger.error("Failed to update company ID [{}] (HTTP 500): {}", id, e.getMessage(), new HttpStatusException(500));
            throw new HttpStatusException(500);
        }
    }

    @Override
    public void deleteCompany(Long id) {
        if (id == null) {
            logger.error("Delete company failed (HTTP 400): ID is null", new HttpStatusException(400));
            throw new HttpStatusException(400);
        }
        try {
            if (!companyRepository.existsById(id)) {
                logger.error("Company not found for ID [{}] (HTTP 404)", id, new HttpStatusException(404));
                throw new HttpStatusException(404);
            }
            companyRepository.deleteById(id);
            logger.info("Deleted company ID [{}]", id);
        } catch (HttpStatusException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Failed to delete company ID [{}] (HTTP 500): {}", id, e.getMessage(), new HttpStatusException(500));
            throw new HttpStatusException(500);
        }
    }

    @Override
    public List<EmployeeDto> findEmployeesByDepartment(Long companyId, String departmentName) {
        if (companyId == null || departmentName == null || departmentName.isEmpty()) {
            logger.error("Find employees by department failed (HTTP 400): Company ID or department name is null/empty", new HttpStatusException(400));
            throw new HttpStatusException(400);
        }
        try {
            List<Employee> employees = companyRepository.findEmployeesByDepartmentName(companyId, departmentName);
            List<EmployeeDto> result = employees.stream()
                    .map(this::convertToEmployeeDto)
                    .toList();
            logger.info("Retrieved employees for company ID [{}] and department [{}]: {} found", companyId, departmentName, result.size());
            return result;
        } catch (Exception e) {
            logger.error("Failed to retrieve employees for company ID [{}] and department [{}] (HTTP 500): {}", companyId, departmentName, e.getMessage(), new HttpStatusException(500));
            throw new HttpStatusException(500);
        }
    }

    @Override
    public List<CompanyDto> findCompaniesWithHighSalaryEmployeesNative(BigDecimal salary) {
        if (salary == null) {
            logger.error("Find companies with high salary employees failed (HTTP 400): Salary is null", new HttpStatusException(400));
            throw new HttpStatusException(400);
        }
        try {
            List<Company> companies = companyRepository.findCompaniesWithHighSalaryEmployeesNative(salary);
            List<CompanyDto> result = companies.stream()
                    .map(companyMapper::toDto)
                    .toList();
            logger.info("Retrieved companies with high salary employees (>{}): {} found", salary, result.size());
            return result;
        } catch (Exception e) {
            logger.error("Failed to retrieve companies with high salary employees (>{}) (HTTP 500): {}", salary, e.getMessage(), new HttpStatusException(500));
            throw new HttpStatusException(500);
        }
    }

    private EmployeeDto convertToEmployeeDto(Employee employee) {
        return EmployeeDto.builder()
                .id(employee.getId())
                .name(employee.getName())
                .salary(employee.getSalary())
                .departmentNames(employee.getEmployeeDepartments().stream()
                        .map(ed -> ed.getDepartment().getName())
                        .toList())
                .build();
    }
}