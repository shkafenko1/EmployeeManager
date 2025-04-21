package by.koronatech.office;

import by.koronatech.office.api.dto.CompanyDto;
import by.koronatech.office.api.dto.EmployeeDto;
import by.koronatech.office.core.exceptions.EntityNotFound;
import by.koronatech.office.core.exceptions.HttpStatusException;
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
    void getAllCompanies_shouldThrowHttpStatusExceptionOnError() {
        when(companyRepository.findAll()).thenThrow(new RuntimeException("Database error"));

        HttpStatusException exception = assertThrows(HttpStatusException.class, () -> companyService.getAllCompanies());
        assertEquals(500, exception.getStatusCode());
        verify(companyRepository).findAll();
        verify(companyMapper, never()).toDtos(any());
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
    void getCompanyById_shouldThrowHttpStatusExceptionForNullId() {
        HttpStatusException exception = assertThrows(HttpStatusException.class, () -> companyService.getCompanyById(null));
        assertEquals(400, exception.getStatusCode());
        verify(companyRepository, never()).findById(any());
        verify(companyMapper, never()).toDto(any());
    }

    @Test
    void getCompanyById_shouldThrowHttpStatusExceptionForNotFound() {
        when(companyRepository.findById(1L)).thenReturn(Optional.empty());

        HttpStatusException exception = assertThrows(HttpStatusException.class, () -> companyService.getCompanyById(1L));
        assertEquals(404, exception.getStatusCode());
        verify(companyRepository).findById(1L);
        verify(companyMapper, never()).toDto(any());
    }

    @Test
    void getCompanyById_shouldThrowHttpStatusExceptionOnError() {
        when(companyRepository.findById(1L)).thenThrow(new RuntimeException("Database error"));

        HttpStatusException exception = assertThrows(HttpStatusException.class, () -> companyService.getCompanyById(1L));
        assertEquals(500, exception.getStatusCode());
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
    void createCompany_shouldThrowHttpStatusExceptionForNullDto() {
        HttpStatusException exception = assertThrows(HttpStatusException.class, () -> companyService.createCompany(null));
        assertEquals(400, exception.getStatusCode());
        verify(companyMapper, never()).toEntity(any());
        verify(companyRepository, never()).save(any());
        verify(companyMapper, never()).toDto(any());
    }

    @Test
    void createCompany_shouldThrowHttpStatusExceptionOnError() {
        when(companyMapper.toEntity(companyDto)).thenReturn(company);
        when(companyRepository.save(company)).thenThrow(new RuntimeException("Database error"));

        HttpStatusException exception = assertThrows(HttpStatusException.class, () -> companyService.createCompany(companyDto));
        assertEquals(500, exception.getStatusCode());
        verify(companyMapper).toEntity(companyDto);
        verify(companyRepository).save(company);
        verify(companyMapper, never()).toDto(any());
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
    void updateCompany_shouldThrowHttpStatusExceptionForNullId() {
        HttpStatusException exception = assertThrows(HttpStatusException.class, () -> companyService.updateCompany(null, companyDto));
        assertEquals(400, exception.getStatusCode());
        verify(companyRepository, never()).findById(any());
        verify(companyMapper, never()).toEntity(any());
        verify(companyRepository, never()).save(any());
        verify(companyMapper, never()).toDto(any());
    }

    @Test
    void updateCompany_shouldThrowHttpStatusExceptionForNullDto() {
        HttpStatusException exception = assertThrows(HttpStatusException.class, () -> companyService.updateCompany(1L, null));
        assertEquals(400, exception.getStatusCode());
        verify(companyRepository, never()).findById(any());
        verify(companyMapper, never()).toEntity(any());
        verify(companyRepository, never()).save(any());
        verify(companyMapper, never()).toDto(any());
    }

    @Test
    void updateCompany_shouldThrowHttpStatusExceptionForNotFound() {
        when(companyRepository.findById(1L)).thenReturn(Optional.empty());

        HttpStatusException exception = assertThrows(HttpStatusException.class, () -> companyService.updateCompany(1L, companyDto));
        assertEquals(404, exception.getStatusCode());
        verify(companyRepository).findById(1L);
        verify(companyMapper, never()).toEntity(any());
        verify(companyRepository, never()).save(any());
        verify(companyMapper, never()).toDto(any());
    }

    @Test
    void updateCompany_shouldThrowHttpStatusExceptionOnError() {
        when(companyRepository.findById(1L)).thenThrow(new RuntimeException("Database error"));

        HttpStatusException exception = assertThrows(HttpStatusException.class, () -> companyService.updateCompany(1L, companyDto));
        assertEquals(500, exception.getStatusCode());
        verify(companyRepository).findById(1L);
        verify(companyMapper, never()).toEntity(any());
        verify(companyRepository, never()).save(any());
        verify(companyMapper, never()).toDto(any());
    }

    @Test
    void deleteCompany_shouldDeleteCompany() {
        Long companyId = 1L;
        when(companyRepository.existsById(companyId)).thenReturn(true);
        doNothing().when(companyRepository).deleteById(companyId);

        companyService.deleteCompany(companyId);

        verify(companyRepository, times(1)).existsById(companyId);
        verify(companyRepository, times(1)).deleteById(companyId);
        verifyNoMoreInteractions(companyRepository);
    }

    @Test
    void deleteCompany_shouldThrowHttpStatusExceptionForNullId() {
        HttpStatusException exception = assertThrows(HttpStatusException.class, () -> companyService.deleteCompany(null));
        assertEquals(400, exception.getStatusCode());
        verify(companyRepository, never()).existsById(any());
        verify(companyRepository, never()).deleteById(any());
    }

    @Test
    void deleteCompany_shouldThrowHttpStatusExceptionForNotFound() {
        Long companyId = 1L;
        when(companyRepository.existsById(companyId)).thenReturn(false);

        HttpStatusException exception = assertThrows(HttpStatusException.class, () -> companyService.deleteCompany(companyId));
        assertEquals(404, exception.getStatusCode());
        verify(companyRepository).existsById(companyId);
        verify(companyRepository, never()).deleteById(any());
    }

    @Test
    void deleteCompany_shouldThrowHttpStatusExceptionOnError() {
        Long companyId = 1L;
        when(companyRepository.existsById(companyId)).thenThrow(new RuntimeException("Database error"));

        HttpStatusException exception = assertThrows(HttpStatusException.class, () -> companyService.deleteCompany(companyId));
        assertEquals(500, exception.getStatusCode());
        verify(companyRepository).existsById(companyId);
        verify(companyRepository, never()).deleteById(any());
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
    void findEmployeesByDepartment_shouldThrowHttpStatusExceptionForNullCompanyId() {
        HttpStatusException exception = assertThrows(HttpStatusException.class, () -> companyService.findEmployeesByDepartment(null, "IT"));
        assertEquals(400, exception.getStatusCode());
        verify(companyRepository, never()).findEmployeesByDepartmentName(any(), any());
    }

    @Test
    void findEmployeesByDepartment_shouldThrowHttpStatusExceptionForNullDepartmentName() {
        HttpStatusException exception = assertThrows(HttpStatusException.class, () -> companyService.findEmployeesByDepartment(1L, null));
        assertEquals(400, exception.getStatusCode());
        verify(companyRepository, never()).findEmployeesByDepartmentName(any(), any());
    }

    @Test
    void findEmployeesByDepartment_shouldThrowHttpStatusExceptionForEmptyDepartmentName() {
        HttpStatusException exception = assertThrows(HttpStatusException.class, () -> companyService.findEmployeesByDepartment(1L, ""));
        assertEquals(400, exception.getStatusCode());
        verify(companyRepository, never()).findEmployeesByDepartmentName(any(), any());
    }

    @Test
    void findEmployeesByDepartment_shouldThrowHttpStatusExceptionOnError() {
        when(companyRepository.findEmployeesByDepartmentName(1L, "IT")).thenThrow(new RuntimeException("Database error"));

        HttpStatusException exception = assertThrows(HttpStatusException.class, () -> companyService.findEmployeesByDepartment(1L, "IT"));
        assertEquals(500, exception.getStatusCode());
        verify(companyRepository).findEmployeesByDepartmentName(1L, "IT");
    }

    @Test
    void findCompaniesWithHighSalaryEmployeesNative_shouldReturnCompanies() {
        BigDecimal salary = new BigDecimal("10000.00");
        Company company = new Company();
        company.setName("Acme Corp");
        List<Company> companies = Collections.singletonList(company);

        CompanyDto companyDto = new CompanyDto();
        companyDto.setName("Acme Corp");

        when(companyRepository.findCompaniesWithHighSalaryEmployeesNative(salary)).thenReturn(companies);
        when(companyMapper.toDto(company)).thenReturn(companyDto);

        List<CompanyDto> result = companyService.findCompaniesWithHighSalaryEmployeesNative(salary);

        assertNotNull(result, "Result list should not be null");
        assertEquals(1, result.size(), "Result list should contain 1 company");
        assertNotNull(result.get(0), "First company should not be null");
        assertEquals("Acme Corp", result.get(0).getName(), "Company name should match");

        verify(companyRepository).findCompaniesWithHighSalaryEmployeesNative(salary);
        verify(companyMapper).toDto(company);
    }

    @Test
    void findCompaniesWithHighSalaryEmployeesNative_shouldThrowHttpStatusExceptionForNullSalary() {
        HttpStatusException exception = assertThrows(HttpStatusException.class, () -> companyService.findCompaniesWithHighSalaryEmployeesNative(null));
        assertEquals(400, exception.getStatusCode());
        verify(companyRepository, never()).findCompaniesWithHighSalaryEmployeesNative(any());
        verify(companyMapper, never()).toDto(any());
    }

    @Test
    void findCompaniesWithHighSalaryEmployeesNative_shouldThrowHttpStatusExceptionOnError() {
        BigDecimal salary = new BigDecimal("10000.00");
        when(companyRepository.findCompaniesWithHighSalaryEmployeesNative(salary)).thenThrow(new RuntimeException("Database error"));

        HttpStatusException exception = assertThrows(HttpStatusException.class, () -> companyService.findCompaniesWithHighSalaryEmployeesNative(salary));
        assertEquals(500, exception.getStatusCode());
        verify(companyRepository).findCompaniesWithHighSalaryEmployeesNative(salary);
        verify(companyMapper, never()).toDto(any());
    }
}