package by.koronatech.office;

import by.koronatech.office.api.dto.CreateEmployeeDto;
import by.koronatech.office.api.dto.EmployeeDto;
import by.koronatech.office.api.dto.UpdateDto;
import by.koronatech.office.core.exceptions.EntityNotFound;
import by.koronatech.office.core.mapper.EmployeeMapper;
import by.koronatech.office.core.model.Department;
import by.koronatech.office.core.model.Employee;
import by.koronatech.office.core.model.EmployeeDepartment;
import by.koronatech.office.core.repository.DepartmentRepository;
import by.koronatech.office.core.repository.EmployeeRepository;
import by.koronatech.office.core.service.impl.EmployeeServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;
import org.springframework.validation.Validator;

import java.math.BigDecimal;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmployeeServiceImplTest {

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private DepartmentRepository departmentRepository;

    @Mock
    private EmployeeMapper employeeMapper;

    @Mock
    private Validator validator;

    @InjectMocks
    private EmployeeServiceImpl employeeService;

    private Employee employee;
    private Department department;
    private CreateEmployeeDto createEmployeeDto;
    private UpdateDto updateDto;

    @BeforeEach
    void setUp() {
        department = new Department();
        department.setId(1L);
        department.setName("IT");

        employee = Employee.builder()
                .id(1L)
                .name("John Doe")
                .salary(new BigDecimal("5000.00"))
                .manager(true)
                .employeeDepartments(new HashSet<>())
                .build();
        EmployeeDepartment ed = new EmployeeDepartment();
        ed.setEmployee(employee);
        ed.setDepartment(department);
        employee.getEmployeeDepartments().add(ed);

        createEmployeeDto = new CreateEmployeeDto();
        createEmployeeDto.setName("John Doe");
        createEmployeeDto.setSalary(new BigDecimal("5000.00"));
        createEmployeeDto.setDepartmentNames(Collections.singletonList("IT"));
        createEmployeeDto.setManager(true);

        updateDto = new UpdateDto();
        updateDto.setName("Jane Doe");
        updateDto.setSalary(new BigDecimal("6000.00"));
        updateDto.setManager(false);
    }

    @Test
    void getAllEmployees_shouldReturnListOfEmployees() {
        when(employeeRepository.findAll()).thenReturn(Collections.singletonList(employee));

        List<EmployeeDto> result = employeeService.getAllEmployees();

        assertEquals(1, result.size());
        assertEquals("John Doe", result.get(0).getName());
        assertEquals(Collections.singletonList("IT"), result.get(0).getDepartmentNames());
        verify(employeeRepository).findAll();
    }

    @Test
    void createEmployee_shouldCreateAndReturnEmployee() {
        when(departmentRepository.findByName("IT")).thenReturn(Optional.of(department));
        when(employeeRepository.save(any(Employee.class))).thenReturn(employee);

        EmployeeDto result = employeeService.createEmployee(createEmployeeDto);

        assertEquals("John Doe", result.getName());
        assertEquals(new BigDecimal("5000.00"), result.getSalary());
        assertTrue(result.isManager());
        verify(departmentRepository).findByName("IT");
        verify(employeeRepository).save(any(Employee.class));
    }

    @Test
    void createEmployee_shouldThrowEntityNotFoundForInvalidDepartment() {
        when(departmentRepository.findByName("IT")).thenReturn(Optional.empty());

        assertThrows(EntityNotFound.class, () -> employeeService.createEmployee(createEmployeeDto));
        verify(departmentRepository).findByName("IT");
        verify(employeeRepository, never()).save(any(Employee.class));
    }

    @Test
    void bulkCreateEmployees_shouldCreateValidAndReturnErrorsForInvalid() {
        // Настройка тестовых данных
        CreateEmployeeDto validDto = new CreateEmployeeDto();
        validDto.setName("John Doe");
        validDto.setSalary(new BigDecimal("5000.00"));
        validDto.setDepartmentNames(Collections.singletonList("IT"));
        validDto.setManager(true);

        CreateEmployeeDto invalidDto = new CreateEmployeeDto();
        invalidDto.setName("");
        invalidDto.setSalary(new BigDecimal("-100"));
        invalidDto.setDepartmentNames(Arrays.asList("IT", "HR", "Finance", "Marketing", "Sales", "Support"));

        List<CreateEmployeeDto> dtos = Arrays.asList(validDto, invalidDto);

        // Мокирование валидатора
        doAnswer(invocation -> {
            CreateEmployeeDto dto = invocation.getArgument(0);
            Errors errors = invocation.getArgument(1);
            if (dto.getName() == null || dto.getName().isEmpty()) {
                errors.rejectValue("name", "name.empty", "Name cannot be empty");
            }
            if (dto.getSalary() != null && dto.getSalary().compareTo(BigDecimal.ZERO) <= 0) {
                errors.rejectValue("salary", "salary.positive", "Salary must be positive");
            }
            if (dto.getDepartmentNames() != null && dto.getDepartmentNames().size() > 5) {
                errors.rejectValue("departmentNames", "departmentNames.max", "Cannot have more than 5 departments");
            }
            return null;
        }).when(validator).validate(any(), any());

        // Мокирование репозиториев
        Department department = new Department();
        department.setId(1L);
        department.setName("IT");
        when(departmentRepository.findByName("IT")).thenReturn(Optional.of(department));
        Employee employee = new Employee();
        employee.setId(1L);
        employee.setName("John Doe");
        when(employeeRepository.save(any(Employee.class))).thenReturn(employee);

        // Выполнение метода
        Map<String, Object> result = employeeService.bulkCreateEmployees(dtos);

        // Проверки
        List<EmployeeDto> created = (List<EmployeeDto>) result.get("created");
        Map<String, Map<String, String>> errors = (Map<String, Map<String, String>>) result.get("errors");

        assertEquals(1, created.size());
        assertEquals("John Doe", created.get(0).getName());
        assertEquals(1, errors.size());
        String invalidKey = errors.keySet().iterator().next();
        assertTrue(invalidKey.startsWith("unknown_"), "Key should start with 'unknown_': " + invalidKey);
        Map<String, String> errorMap = errors.get(invalidKey);
        assertEquals("Name cannot be empty", errorMap.get("name"));
        assertEquals("Salary must be positive", errorMap.get("salary"));
        assertEquals("Cannot have more than 5 departments", errorMap.get("departmentNames"));
    }

    @Test
    void bulkCreateEmployees_shouldHandleInvalidDepartment() {
        CreateEmployeeDto invalidDto = new CreateEmployeeDto();
        invalidDto.setName("Jane Doe");
        invalidDto.setSalary(new BigDecimal("6000.00"));
        invalidDto.setDepartmentNames(Collections.singletonList("Nonexistent"));

        List<CreateEmployeeDto> dtos = Arrays.asList(createEmployeeDto, invalidDto);

        doNothing().when(validator).validate(any(), any());
        when(departmentRepository.findByName("IT")).thenReturn(Optional.of(department));
        when(departmentRepository.findByName("Nonexistent")).thenReturn(Optional.empty());
        when(employeeRepository.save(any(Employee.class))).thenReturn(employee);

        Map<String, Object> result = employeeService.bulkCreateEmployees(dtos);

        List<EmployeeDto> created = (List<EmployeeDto>) result.get("created");
        Map<String, Map<String, String>> errors = (Map<String, Map<String, String>>) result.get("errors");

        assertEquals(1, created.size());
        assertEquals("John Doe", created.get(0).getName());
        assertEquals(1, errors.size());
        assertTrue(errors.containsKey("Jane Doe"));
        assertEquals("Department Nonexistent not found", errors.get("Jane Doe").get("general"));
        verify(departmentRepository).findByName("IT");
        verify(departmentRepository).findByName("Nonexistent");
        verify(employeeRepository).save(any(Employee.class));
    }

    @Test
    void bulkCreateEmployees_shouldHandleEmptyList() {
        List<CreateEmployeeDto> dtos = Collections.emptyList();

        Map<String, Object> result = employeeService.bulkCreateEmployees(dtos);

        List<EmployeeDto> created = (List<EmployeeDto>) result.get("created");
        Map<String, Map<String, String>> errors = (Map<String, Map<String, String>>) result.get("errors");
        assertNotNull(created, "Created list should not be null");
        assertTrue(created.isEmpty(), "Created list should be empty");
        assertNotNull(errors, "Errors map should not be null");
        assertTrue(errors.isEmpty(), "Errors map should be empty");
        verifyNoInteractions(validator, employeeRepository, employeeMapper);
    }

    @Test
    void bulkCreateEmployees_shouldHandleValidationErrors() {
        CreateEmployeeDto dto = new CreateEmployeeDto();
        dto.setName("John Doe");
        dto.setSalary(new BigDecimal("-5000.00"));
        List<CreateEmployeeDto> dtos = Collections.singletonList(dto);

        BeanPropertyBindingResult validationErrors = new BeanPropertyBindingResult(dto, "employeeDto");
        validationErrors.rejectValue("salary", "salary.negative", "Salary must be positive");
        validationErrors.rejectValue("name", "name.invalid", null);

        doAnswer(invocation -> {
            BeanPropertyBindingResult errors = invocation.getArgument(1);
            errors.addError(new FieldError("employeeDto", "salary", "Salary must be positive"));
            errors.addError(new FieldError("employeeDto", "name", null));
            return null;
        }).when(validator).validate(eq(dto), any(BeanPropertyBindingResult.class));

        Map<String, Object> result = employeeService.bulkCreateEmployees(dtos);

        List<EmployeeDto> created = (List<EmployeeDto>) result.get("created");
        Map<String, Map<String, String>> errors = (Map<String, Map<String, String>>) result.get("errors");
        assertTrue(created.isEmpty(), "Created list should be empty");
        assertEquals(1, errors.size(), "Errors map should contain 1 entry");
        assertEquals("Salary must be positive", errors.get("John Doe").get("salary"));
        assertEquals("Validation error", errors.get("John Doe").get("name"));
        verify(validator).validate(eq(dto), any(BeanPropertyBindingResult.class));
        verifyNoInteractions(employeeRepository, employeeMapper);
    }

    @Test
    void bulkCreateEmployees_shouldHandleNullName() {
        CreateEmployeeDto dto = new CreateEmployeeDto();
        dto.setName(null);
        dto.setSalary(new BigDecimal("5000.00"));
        List<CreateEmployeeDto> dtos = Collections.singletonList(dto);

        BeanPropertyBindingResult validationErrors = new BeanPropertyBindingResult(dto, "employeeDto");
        validationErrors.rejectValue("name", "name.null", "Name cannot be null");

        doAnswer(invocation -> {
            BeanPropertyBindingResult errors = invocation.getArgument(1);
            errors.addError(new FieldError("employeeDto", "name", "Name cannot be null"));
            return null;
        }).when(validator).validate(eq(dto), any(BeanPropertyBindingResult.class));

        Map<String, Object> result = employeeService.bulkCreateEmployees(dtos);

        List<EmployeeDto> created = (List<EmployeeDto>) result.get("created");
        Map<String, Map<String, String>> errors = (Map<String, Map<String, String>>) result.get("errors");
        assertTrue(created.isEmpty(), "Created list should be empty");
        assertEquals(1, errors.size(), "Errors map should contain 1 entry");
        assertTrue(errors.containsKey("unknown_" + errors.keySet().iterator().next().split("_")[1]), "Key should be UUID");
        assertEquals("Name cannot be null", errors.values().iterator().next().get("name"));
        verify(validator).validate(eq(dto), any(BeanPropertyBindingResult.class));
        verifyNoInteractions(employeeRepository, employeeMapper);
    }

    @Test
    void bulkCreateEmployees_shouldHandleEntityNotFound() {
        CreateEmployeeDto dto = new CreateEmployeeDto();
        dto.setName("John Doe");
        dto.setSalary(new BigDecimal("5000.00"));
        List<CreateEmployeeDto> dtos = Collections.singletonList(dto);

        System.out.println("EmployeeMapper mock: " + employeeMapper); // Debug: Should print Mockito mock
        doNothing().when(validator).validate(any(), any(BeanPropertyBindingResult.class));

        Map<String, Object> result = employeeService.bulkCreateEmployees(dtos);

        List<EmployeeDto> created = (List<EmployeeDto>) result.get("created");
        Map<String, Map<String, String>> errors = (Map<String, Map<String, String>>) result.get("errors");
        assertTrue(created.isEmpty(), "Created list should be empty");
        assertEquals(1, errors.size(), "Errors map should contain 1 entry");
        verify(validator).validate(any(), any(BeanPropertyBindingResult.class));
    }


    @Test
    void findAllEmployeesByDepartment_shouldReturnEmployees() {
        Page<Employee> page = new PageImpl<>(Collections.singletonList(employee));
        when(employeeRepository.findByEmployeeDepartmentsDepartmentName("IT", PageRequest.of(0, Integer.MAX_VALUE)))
                .thenReturn(page);

        List<EmployeeDto> result = employeeService.findAllEmployeesByDepartment("IT");

        assertEquals(1, result.size());
        assertEquals("John Doe", result.get(0).getName());
        verify(employeeRepository).findByEmployeeDepartmentsDepartmentName("IT", PageRequest.of(0, Integer.MAX_VALUE));
    }

    @Test
    void updateEmployee_shouldUpdateAndReturnEmployee() {
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));
        when(employeeRepository.save(any(Employee.class))).thenReturn(employee);

        EmployeeDto result = employeeService.updateEmployee(1L, updateDto);

        assertEquals("Jane Doe", result.getName());
        assertEquals(new BigDecimal("6000.00"), result.getSalary());
        assertFalse(result.isManager());
        verify(employeeRepository).findById(1L);
        verify(employeeRepository).save(any(Employee.class));
    }

    @Test
    void updateEmployee_shouldThrowEntityNotFoundForInvalidId() {
        when(employeeRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFound.class, () -> employeeService.updateEmployee(1L, updateDto));
        verify(employeeRepository).findById(1L);
        verify(employeeRepository, never()).save(any(Employee.class));
    }

    @Test
    void deleteEmployee_shouldDeleteEmployee() {
        when(employeeRepository.existsById(1L)).thenReturn(true);
        doNothing().when(employeeRepository).deleteById(1L);

        employeeService.deleteEmployee(1L);

        verify(employeeRepository).existsById(1L);
        verify(employeeRepository).deleteById(1L);
    }

    @Test
    void deleteEmployee_shouldThrowEntityNotFound() {
        when(employeeRepository.existsById(1L)).thenReturn(false);

        assertThrows(EntityNotFound.class, () -> employeeService.deleteEmployee(1L));
        verify(employeeRepository).existsById(1L);
        verify(employeeRepository, never()).deleteById(1L);
    }

    @Test
    void findEmployeeById_shouldReturnEmployee() {
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));

        EmployeeDto result = employeeService.findEmployeeById(1L);

        assertEquals("John Doe", result.getName());
        assertEquals(Collections.singletonList("IT"), result.getDepartmentNames());
        verify(employeeRepository).findById(1L);
    }

    @Test
    void findEmployeeById_shouldThrowEntityNotFound() {
        when(employeeRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFound.class, () -> employeeService.findEmployeeById(1L));
        verify(employeeRepository).findById(1L);
    }
}