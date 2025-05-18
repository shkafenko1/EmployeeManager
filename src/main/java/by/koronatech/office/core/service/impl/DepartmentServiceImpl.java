package by.koronatech.office.core.service.impl;

import by.koronatech.office.api.dto.DepartmentDto;
import by.koronatech.office.api.dto.DepartmentReturnDto;
import by.koronatech.office.core.exceptions.EntityNotFound;
import by.koronatech.office.core.exceptions.HttpStatusException;
import by.koronatech.office.core.mapper.DepartmentMapper;
import by.koronatech.office.core.mapper.DepartmentReturnMapper;
import by.koronatech.office.core.model.Company;
import by.koronatech.office.core.model.Department;
import by.koronatech.office.core.model.Employee;
import by.koronatech.office.core.model.EmployeeDepartment;
import by.koronatech.office.core.repository.CompanyRepository;
import by.koronatech.office.core.repository.DepartmentRepository;
import by.koronatech.office.core.repository.EmployeeRepository;
import by.koronatech.office.core.service.DepartmentService;
import jakarta.transaction.Transactional;
import java.util.List;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class DepartmentServiceImpl implements DepartmentService {
    private static final Logger logger = LoggerFactory.getLogger(DepartmentServiceImpl.class);

    private final CompanyRepository companyRepository;
    private final DepartmentMapper departmentMapper;
    private final DepartmentReturnMapper departmentReturnMapper;

    @Autowired
    private final EmployeeServiceImpl employeeService;

    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Override
    public List<DepartmentReturnDto> getAllDepartments() {
        try {
            List<DepartmentReturnDto> departments = departmentReturnMapper
                    .toDtos(departmentRepository.findAll());
            logger.info("Retrieved all departments: {} found", departments.size());
            return departments;
        } catch (Exception e) {
            logger.error("Failed to retrieve departments (HTTP 500): {}", e.getMessage(), e);
            throw new HttpStatusException(500);
        }
    }

    @Override
    public DepartmentReturnDto getDepartmentById(Long id) {
        if (id == null) {
            logger.error("Get department failed (HTTP 400): ID is null");
            throw new HttpStatusException(400);
        }
        try {
            Department department = departmentRepository.findById(id)
                    .orElseThrow(() -> new EntityNotFound("Department not found with id: " + id));
            DepartmentReturnDto result = departmentReturnMapper.toDto(department);
            logger.info("Retrieved department ID [{}]", id);
            return result;
        } catch (EntityNotFound e) {
            logger.error("Department not found for ID [{}] (HTTP 404): {}", id, e.getMessage(), e);
            throw new HttpStatusException(404);
        } catch (Exception e) {
            logger.error("Failed to retrieveabban department ID [{}] (HTTP 500): {}", id, e.getMessage(), e);
            throw new HttpStatusException(500);
        }
    }

    @Override
    public DepartmentDto createDepartment(DepartmentDto departmentDto) {
        if (departmentDto == null) {
            logger.error("Create department failed (HTTP 400): DTO is null");
            throw new HttpStatusException(400);
        }
        try {
            Department department = departmentMapper.toEntity(departmentDto, companyRepository);
            Department savedDepartment = departmentRepository.save(department);
            DepartmentDto result = departmentMapper.toDto(savedDepartment);
            logger.info("Created department [{}]", result.getName());
            return result;
        } catch (EntityNotFound e) {
            logger.error("Entity not found for department [{}] (HTTP 404): {}", departmentDto.getName(), e.getMessage(), e);
            throw new HttpStatusException(404);
        } catch (Exception e) {
            logger.error("Failed to create department [{}] (HTTP 500): {}", departmentDto.getName(), e.getMessage(), e);
            throw new HttpStatusException(500);
        }
    }

    @Override
    @Transactional
    public DepartmentDto updateDepartment(Long id, DepartmentDto updatedDepartmentDto) {
        if (id == null || updatedDepartmentDto == null) {
            logger.error("Update department failed (HTTP 400): ID or DTO is null");
            throw new HttpStatusException(400);
        }
        try {
            Department existingDepartment = departmentRepository.findById(id)
                    .orElseThrow(() -> new EntityNotFound("Department not found with id: " + id));
            existingDepartment.setName(updatedDepartmentDto.getName());
            Company company = companyRepository.findByName(updatedDepartmentDto.getCompany())
                    .orElseThrow(() -> new EntityNotFound("Company not found: " + updatedDepartmentDto.getCompany()));
            existingDepartment.setCompany(company);
            Department savedDepartment = departmentRepository.save(existingDepartment);
            DepartmentDto result = departmentMapper.toDto(savedDepartment);
            logger.info("Updated department ID [{}]", id);
            return result;
        } catch (EntityNotFound e) {
            logger.error("Entity not found for department ID [{}] (HTTP 404): {}", id, e.getMessage(), e);
            throw new HttpStatusException(404);
        } catch (Exception e) {
            logger.error("Failed to update department ID [{}] (HTTP 500): {}", id, e.getMessage(), e);
            throw new HttpStatusException(500);
        }
    }

    @Override
    @Transactional
    public void deleteDepartment(Long id) {
        if (id == null) {
            logger.error("Delete department failed (HTTP 400): ID is null");
            throw new HttpStatusException(400);
        }
        try {
            // Load department with employeeDepartments
            Department department = departmentRepository.findById(id)
                    .orElseThrow(() -> new EntityNotFound("Department not found: " + id));
            // Process employeeDepartments
            for (EmployeeDepartment ed : department.getEmployeeDepartments()) {
                Employee employee = ed.getEmployee();
                logger.info("Processing employee ID [{}] for department ID [{}]", employee.getId(), id);
                // Clear employee's employeeDepartments to avoid constraints
                employee.getEmployeeDepartments().clear();
                employeeRepository.save(employee); // Update employee to persist cleared associations
                // Delete the employee
                employeeRepository.delete(employee);
                logger.info("Deleted employee ID [{}] for department ID [{}]", employee.getId(), id);
            }
            // Delete the department (cascades to employeeDepartments)
            departmentRepository.deleteById(id);
            logger.info("Deleted department ID [{}]", id);
        } catch (HttpStatusException e) {
            logger.error("HttpStatusException while deleting department ID [{}] : {}", id, e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("Failed to delete department ID [{}] (HTTP 500): {}", id, e.getMessage(), e);
            throw new HttpStatusException(500);
        }
    }
}