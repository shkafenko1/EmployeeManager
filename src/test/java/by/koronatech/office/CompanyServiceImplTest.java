package by.koronatech.office;

import by.koronatech.office.api.dto.CompanyDto;
import by.koronatech.office.api.dto.EmployeeDto;
import by.koronatech.office.core.exceptions.EntityNotFound;
import by.koronatech.office.core.mapper.CompanyMapper;
import by.koronatech.office.core.model.Company;
import by.koronatech.office.core.model.Employee;
import by.koronatech.office.core.repository.CompanyRepository;
import by.koronatech.office.core.service.impl.CompanyServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CompanyServiceImplTest {

    @Mock
    private CompanyRepository companyRepository;

    @Mock
    private CompanyMapper companyMapper;

    @InjectMocks
    private CompanyServiceImpl companyService;

    private Company company;
    private CompanyDto companyDto;
    private Employee employee;

    @BeforeEach
    void setUp() {
        company = new Company();
        company.setId(1L);
        company.setName("Test Company");
        company.setLocation("Test Location");

        companyDto = new CompanyDto();
        companyDto.setName("Test Company");
        companyDto.setLocation("Test Location");

        employee = Employee.builder()
                .id(1L)
                .name("John Doe")
                .salary(new BigDecimal("5000.00"))
                .employeeDepartments(new HashSet<>())
                .build();
    }

    @Test
    void getAllCompanies_shouldReturnListOfCompanies() {
        when(companyRepository.findAll()).thenReturn(Collections.singletonList(company));
        when(companyMapper.toDtos(any())).thenReturn(Collections.singletonList(companyDto));

        List<CompanyDto> result = companyService.getAllCompanies();

        assertEquals(1, result.size());
        assertEquals("Test Company", result.get(0).getName());
        verify(companyRepository).findAll();
        verify(companyMapper).toDtos(any());
    }

    @Test
    void getCompanyById_shouldReturnCompany() {
        when(companyRepository.findById(1L)).thenReturn(Optional.of(company));
        when(companyMapper.toDto(company)).thenReturn(companyDto);

        CompanyDto result = companyService.getCompanyById(1L);

        assertEquals("Test Company", result.getName());
        verify(companyRepository).findById(1L);
        verify(companyMapper).toDto(company);
    }

    @Test
    void getCompanyById_shouldThrowEntityNotFound() {
        when(companyRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFound.class, () -> companyService.getCompanyById(1L));
        verify(companyRepository).findById(1L);
        verify(companyMapper, never()).toDto(any());
    }

    @Test
    void createCompany_shouldCreateAndReturnCompany() {
        when(companyMapper.toEntity(companyDto)).thenReturn(company);
        when(companyRepository.save(company)).thenReturn(company);
        when(companyMapper.toDto(company)).thenReturn(companyDto);

        CompanyDto result = companyService.createCompany(companyDto);

        assertEquals("Test Company", result.getName());
        verify(companyMapper).toEntity(companyDto);
        verify(companyRepository).save(company);
        verify(companyMapper).toDto(company);
    }

    @Test
    void updateCompany_shouldUpdateAndReturnCompany() {
        Company updatedCompany = new Company();
        updatedCompany.setId(1L);
        updatedCompany.setName("Updated Company");
        updatedCompany.setLocation("Updated Location");

        when(companyRepository.findById(1L)).thenReturn(Optional.of(company));
        when(companyMapper.toEntity(companyDto)).thenReturn(updatedCompany);
        when(companyRepository.save(updatedCompany)).thenReturn(updatedCompany);
        when(companyMapper.toDto(updatedCompany)).thenReturn(companyDto);

        CompanyDto result = companyService.updateCompany(1L, companyDto);

        assertEquals("Test Company", result.getName());
        verify(companyRepository).findById(1L);
        verify(companyMapper).toEntity(companyDto);
        verify(companyRepository).save(updatedCompany);
        verify(companyMapper).toDto(updatedCompany);
    }

    @Test
    void updateCompany_shouldThrowEntityNotFound() {
        when(companyRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFound.class, () -> companyService.updateCompany(1L, companyDto));
        verify(companyRepository).findById(1L);
        verify(companyMapper, never()).toEntity(any());
        verify(companyRepository, never()).save(any());
    }

    @Test
    void deleteCompany_shouldDeleteCompany() {
        doNothing().when(companyRepository).deleteById(1L);

        companyService.deleteCompany(1L);

        verify(companyRepository).deleteById(1L);
    }

    @Test
    void findEmployeesByDepartment_shouldReturnEmployees() {
        when(companyRepository.findEmployeesByDepartmentName(1L, "IT"))
                .thenReturn(Collections.singletonList(employee));

        List<EmployeeDto> result = companyService.findEmployeesByDepartment(1L, "IT");

        assertEquals(1, result.size());
        assertEquals("John Doe", result.get(0).getName());
        verify(companyRepository).findEmployeesByDepartmentName(1L, "IT");
    }

    @Test
    void findCompaniesWithHighSalaryEmployeesNative_shouldReturnCompanies() {
        // Настройка тестовых данных
        BigDecimal salary = new BigDecimal("10000.00");

        Company company = new Company();
        company.setName("Acme Corp");
        List<Company> companies = Collections.singletonList(company);

        CompanyDto companyDto = new CompanyDto();
        companyDto.setName("Acme Corp");

        // Настройка моков
        when(companyRepository.findCompaniesWithHighSalaryEmployeesNative(salary)).thenReturn(companies);
        when(companyMapper.toDto(company)).thenReturn(companyDto);

        // Выполнение метода
        List<CompanyDto> result = companyService.findCompaniesWithHighSalaryEmployeesNative(salary);

        // Проверки
        assertNotNull(result, "Result list should not be null");
        assertEquals(1, result.size(), "Result list should contain 1 company");
        assertNotNull(result.get(0), "First company should not be null");
        assertEquals("Acme Corp", result.get(0).getName(), "Company name should match");

        // Проверка вызовов
        verify(companyRepository).findCompaniesWithHighSalaryEmployeesNative(salary);
        verify(companyMapper).toDto(company);
    }
}