package by.koronatech.office;

import by.koronatech.office.api.dto.DepartmentDto;
import by.koronatech.office.core.exceptions.EntityNotFound;
import by.koronatech.office.core.exceptions.HttpStatusException;
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
    void getAllDepartments_shouldThrowHttpStatusExceptionOnError() {
        when(departmentRepository.findAll()).thenThrow(new RuntimeException("Database error"));

        HttpStatusException exception = assertThrows(HttpStatusException.class, () -> departmentService.getAllDepartments());
        assertEquals(500, exception.getStatusCode());
        verify(departmentRepository).findAll();
        verify(departmentMapper, never()).toDtos(any());
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
    void getDepartmentById_shouldThrowHttpStatusExceptionForNullId() {
        HttpStatusException exception = assertThrows(HttpStatusException.class, () -> departmentService.getDepartmentById(null));
        assertEquals(400, exception.getStatusCode());
        verify(departmentRepository, never()).findById(any());
        verify(departmentMapper, never()).toDto(any());
    }

    @Test
    void getDepartmentById_shouldThrowHttpStatusExceptionForNotFound() {
        when(departmentRepository.findById(1L)).thenReturn(Optional.empty());

        HttpStatusException exception = assertThrows(HttpStatusException.class, () -> departmentService.getDepartmentById(1L));
        assertEquals(404, exception.getStatusCode());
        verify(departmentRepository).findById(1L);
        verify(departmentMapper, never()).toDto(any());
    }

    @Test
    void getDepartmentById_shouldThrowHttpStatusExceptionOnError() {
        when(departmentRepository.findById(1L)).thenThrow(new RuntimeException("Database error"));

        HttpStatusException exception = assertThrows(HttpStatusException.class, () -> departmentService.getDepartmentById(1L));
        assertEquals(500, exception.getStatusCode());
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
    void createDepartment_shouldThrowHttpStatusExceptionForNullDto() {
        HttpStatusException exception = assertThrows(HttpStatusException.class, () -> departmentService.createDepartment(null));
        assertEquals(400, exception.getStatusCode());
        verify(departmentMapper, never()).toEntity(any(), any());
        verify(departmentRepository, never()).save(any());
        verify(departmentMapper, never()).toDto(any());
    }

    @Test
    void createDepartment_shouldThrowHttpStatusExceptionForEntityNotFound() {
        when(departmentMapper.toEntity(departmentDto, companyRepository)).thenThrow(new EntityNotFound("Company not found"));

        HttpStatusException exception = assertThrows(HttpStatusException.class, () -> departmentService.createDepartment(departmentDto));
        assertEquals(404, exception.getStatusCode());
        verify(departmentMapper).toEntity(departmentDto, companyRepository);
        verify(departmentRepository, never()).save(any());
        verify(departmentMapper, never()).toDto(any());
    }

    @Test
    void createDepartment_shouldThrowHttpStatusExceptionOnError() {
        when(departmentMapper.toEntity(departmentDto, companyRepository)).thenReturn(department);
        when(departmentRepository.save(department)).thenThrow(new RuntimeException("Database error"));

        HttpStatusException exception = assertThrows(HttpStatusException.class, () -> departmentService.createDepartment(departmentDto));
        assertEquals(500, exception.getStatusCode());
        verify(departmentMapper).toEntity(departmentDto, companyRepository);
        verify(departmentRepository).save(department);
        verify(departmentMapper, never()).toDto(any());
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
    void updateDepartment_shouldThrowHttpStatusExceptionForNullId() {
        HttpStatusException exception = assertThrows(HttpStatusException.class, () -> departmentService.updateDepartment(null, departmentDto));
        assertEquals(400, exception.getStatusCode());
        verify(departmentRepository, never()).findById(any());
        verify(companyRepository, never()).findByName(any());
        verify(departmentRepository, never()).save(any());
        verify(departmentMapper, never()).toDto(any());
    }

    @Test
    void updateDepartment_shouldThrowHttpStatusExceptionForNullDto() {
        HttpStatusException exception = assertThrows(HttpStatusException.class, () -> departmentService.updateDepartment(1L, null));
        assertEquals(400, exception.getStatusCode());
        verify(departmentRepository, never()).findById(any());
        verify(companyRepository, never()).findByName(any());
        verify(departmentRepository, never()).save(any());
        verify(departmentMapper, never()).toDto(any());
    }

    @Test
    void updateDepartment_shouldThrowHttpStatusExceptionForInvalidDepartment() {
        when(departmentRepository.findById(1L)).thenReturn(Optional.empty());

        HttpStatusException exception = assertThrows(HttpStatusException.class, () -> departmentService.updateDepartment(1L, departmentDto));
        assertEquals(404, exception.getStatusCode());
        verify(departmentRepository).findById(1L);
        verify(companyRepository, never()).findByName(any());
        verify(departmentRepository, never()).save(any());
        verify(departmentMapper, never()).toDto(any());
    }

    @Test
    void updateDepartment_shouldThrowHttpStatusExceptionForInvalidCompany() {
        when(departmentRepository.findById(1L)).thenReturn(Optional.of(department));
        when(companyRepository.findByName("Test Company")).thenReturn(Optional.empty());

        HttpStatusException exception = assertThrows(HttpStatusException.class, () -> departmentService.updateDepartment(1L, departmentDto));
        assertEquals(404, exception.getStatusCode());
        verify(departmentRepository).findById(1L);
        verify(companyRepository).findByName("Test Company");
        verify(departmentRepository, never()).save(any());
        verify(departmentMapper, never()).toDto(any());
    }

    @Test
    void updateDepartment_shouldThrowHttpStatusExceptionOnError() {
        when(departmentRepository.findById(1L)).thenThrow(new RuntimeException("Database error"));

        HttpStatusException exception = assertThrows(HttpStatusException.class, () -> departmentService.updateDepartment(1L, departmentDto));
        assertEquals(500, exception.getStatusCode());
        verify(departmentRepository).findById(1L);
        verify(companyRepository, never()).findByName(any());
        verify(departmentRepository, never()).save(any());
        verify(departmentMapper, never()).toDto(any());
    }

    @Test
    void deleteDepartment_shouldDeleteDepartment() {
        Long departmentId = 1L;
        when(departmentRepository.existsById(departmentId)).thenReturn(true);
        doNothing().when(departmentRepository).deleteById(departmentId);

        departmentService.deleteDepartment(departmentId);

        verify(departmentRepository).existsById(departmentId);
        verify(departmentRepository).deleteById(departmentId);
        verifyNoMoreInteractions(departmentRepository);
    }

    @Test
    void deleteDepartment_shouldThrowHttpStatusExceptionForNullId() {
        HttpStatusException exception = assertThrows(HttpStatusException.class, () -> departmentService.deleteDepartment(null));
        assertEquals(400, exception.getStatusCode());
        verify(departmentRepository, never()).existsById(any());
        verify(departmentRepository, never()).deleteById(any());
    }

    @Test
    void deleteDepartment_shouldThrowHttpStatusExceptionForNotFound() {
        Long departmentId = 1L;
        when(departmentRepository.existsById(departmentId)).thenReturn(false);

        HttpStatusException exception = assertThrows(HttpStatusException.class, () -> departmentService.deleteDepartment(departmentId));
        assertEquals(404, exception.getStatusCode());
        verify(departmentRepository).existsById(departmentId);
        verify(departmentRepository, never()).deleteById(any());
    }

    @Test
    void deleteDepartment_shouldThrowHttpStatusExceptionOnError() {
        Long departmentId = 1L;
        when(departmentRepository.existsById(departmentId)).thenThrow(new RuntimeException("Database error"));

        HttpStatusException exception = assertThrows(HttpStatusException.class, () -> departmentService.deleteDepartment(departmentId));
        assertEquals(500, exception.getStatusCode());
        verify(departmentRepository).existsById(departmentId);
        verify(departmentRepository, never()).deleteById(any());
    }
}