package by.koronatech.office;

import by.koronatech.office.api.dto.DepartmentDto;
import by.koronatech.office.core.exceptions.EntityNotFound;
import by.koronatech.office.core.mapper.DepartmentMapper;
import by.koronatech.office.core.model.Company;
import by.koronatech.office.core.model.Department;
import by.koronatech.office.core.repository.CompanyRepository;
import by.koronatech.office.core.repository.DepartmentRepository;
import by.koronatech.office.core.service.impl.DepartmentServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DepartmentServiceImplTest {

    @Mock
    private DepartmentRepository departmentRepository;

    @Mock
    private CompanyRepository companyRepository;

    @Mock
    private DepartmentMapper departmentMapper;

    @InjectMocks
    private DepartmentServiceImpl departmentService;

    private Department department;
    private DepartmentDto departmentDto;
    private Company company;

    @BeforeEach
    void setUp() {
        company = new Company();
        company.setId(1L);
        company.setName("Test Company");

        department = new Department();
        department.setId(1L);
        department.setName("IT");
        department.setCompany(company);

        departmentDto = new DepartmentDto();
        departmentDto.setName("IT");
        departmentDto.setCompany("Test Company");
    }

    @Test
    void getAllDepartments_shouldReturnListOfDepartments() {
        when(departmentRepository.findAll()).thenReturn(Collections.singletonList(department));
        when(departmentMapper.toDtos(any())).thenReturn(Collections.singletonList(departmentDto));

        List<DepartmentDto> result = departmentService.getAllDepartments();

        assertEquals(1, result.size());
        assertEquals("IT", result.get(0).getName());
        verify(departmentRepository).findAll();
        verify(departmentMapper).toDtos(any());
    }

    @Test
    void getDepartmentById_shouldReturnDepartment() {
        when(departmentRepository.findById(1L)).thenReturn(Optional.of(department));
        when(departmentMapper.toDto(department)).thenReturn(departmentDto);

        DepartmentDto result = departmentService.getDepartmentById(1L);

        assertEquals("IT", result.getName());
        verify(departmentRepository).findById(1L);
        verify(departmentMapper).toDto(department);
    }

    @Test
    void getDepartmentById_shouldThrowEntityNotFound() {
        when(departmentRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFound.class, () -> departmentService.getDepartmentById(1L));
        verify(departmentRepository).findById(1L);
        verify(departmentMapper, never()).toDto(any());
    }

    @Test
    void createDepartment_shouldCreateAndReturnDepartment() {
        when(departmentMapper.toEntity(departmentDto, companyRepository)).thenReturn(department);
        when(departmentRepository.save(department)).thenReturn(department);
        when(departmentMapper.toDto(department)).thenReturn(departmentDto);

        DepartmentDto result = departmentService.createDepartment(departmentDto);

        assertEquals("IT", result.getName());
        verify(departmentMapper).toEntity(departmentDto, companyRepository);
        verify(departmentRepository).save(department);
        verify(departmentMapper).toDto(department);
    }

    @Test
    void updateDepartment_shouldUpdateAndReturnDepartment() {
        when(departmentRepository.findById(1L)).thenReturn(Optional.of(department));
        when(companyRepository.findByName("Test Company")).thenReturn(Optional.of(company));
        when(departmentRepository.save(department)).thenReturn(department);
        when(departmentMapper.toDto(department)).thenReturn(departmentDto);

        DepartmentDto result = departmentService.updateDepartment(1L, departmentDto);

        assertEquals("IT", result.getName());
        verify(departmentRepository).findById(1L);
        verify(companyRepository).findByName("Test Company");
        verify(departmentRepository).save(department);
        verify(departmentMapper).toDto(department);
    }

    @Test
    void updateDepartment_shouldThrowEntityNotFoundForInvalidDepartment() {
        when(departmentRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFound.class, () -> departmentService.updateDepartment(1L, departmentDto));
        verify(departmentRepository).findById(1L);
        verify(companyRepository, never()).findByName(any());
        verify(departmentRepository, never()).save(any());
    }

    @Test
    void updateDepartment_shouldThrowEntityNotFoundForInvalidCompany() {
        when(departmentRepository.findById(1L)).thenReturn(Optional.of(department));
        when(companyRepository.findByName("Test Company")).thenReturn(Optional.empty());

        assertThrows(EntityNotFound.class, () -> departmentService.updateDepartment(1L, departmentDto));
        verify(departmentRepository).findById(1L);
        verify(companyRepository).findByName("Test Company");
        verify(departmentRepository, never()).save(any());
    }

    @Test
    void deleteDepartment_shouldDeleteDepartment() {
        doNothing().when(departmentRepository).deleteById(1L);

        departmentService.deleteDepartment(1L);

        verify(departmentRepository).deleteById(1L);
    }
}