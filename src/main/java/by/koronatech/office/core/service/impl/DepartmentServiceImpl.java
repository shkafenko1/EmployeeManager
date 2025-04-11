package by.koronatech.office.core.service.impl;

import by.koronatech.office.api.dto.DepartmentDto;
import by.koronatech.office.core.exceptions.EntityNotFound;
import by.koronatech.office.core.mapper.DepartmentMapper;
import by.koronatech.office.core.model.Company;
import by.koronatech.office.core.model.Department;
import by.koronatech.office.core.repository.CompanyRepository;
import by.koronatech.office.core.repository.DepartmentRepository;
import by.koronatech.office.core.service.DepartmentService;
import jakarta.transaction.Transactional;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class DepartmentServiceImpl implements DepartmentService {

    private final DepartmentRepository departmentRepository;
    private final CompanyRepository companyRepository;
    private final DepartmentMapper departmentMapper;

    @Override
    public List<DepartmentDto> getAllDepartments() {
        return departmentMapper.toDtos(departmentRepository.findAll());
    }

    @Override
    public DepartmentDto getDepartmentById(Long id) {
        Department department = departmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Department not found with id: " + id));
        return departmentMapper.toDto(department);
    }

    @Override
    public DepartmentDto createDepartment(DepartmentDto departmentDto) {
        // The mapper uses companyRepository to convert the company name to a Company.
        Department department = departmentMapper.toEntity(departmentDto, companyRepository);
        Department savedDepartment = departmentRepository.save(department);
        return departmentMapper.toDto(savedDepartment);
    }

    @Override
    @Transactional
    public DepartmentDto updateDepartment(Long id, DepartmentDto updatedDepartmentDto) {
        Department existingDepartment = departmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Department not found with id: " + id));

        // Update fields manually
        existingDepartment.setName(updatedDepartmentDto.getName());
        Company company = companyRepository.findByName(updatedDepartmentDto.getCompany())
                .orElseThrow(() -> new EntityNotFound("Company not found: "
                        + updatedDepartmentDto.getCompany()));
        existingDepartment.setCompany(company);

        Department savedDepartment = departmentRepository.save(existingDepartment);
        return departmentMapper.toDto(savedDepartment);
    }

    @Override
    public void deleteDepartment(Long id) {
        departmentRepository.deleteById(id);
    }
}