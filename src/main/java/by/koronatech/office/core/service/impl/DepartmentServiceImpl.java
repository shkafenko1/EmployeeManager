package by.koronatech.office.core.service.impl;

import by.koronatech.office.api.dto.DepartmentDto;
import by.koronatech.office.core.mapper.DepartmentMapper;
import by.koronatech.office.core.model.Department;
import by.koronatech.office.core.repository.DepartmentRepository;
import by.koronatech.office.core.service.DepartmentService;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class DepartmentServiceImpl implements DepartmentService {

    private final DepartmentRepository departmentRepository;
    private final DepartmentMapper departmentMapper;

    @Override
    public List<DepartmentDto> getAllDepartments() {
        return departmentMapper
                .toDtos(departmentRepository.findAll());
    }

    @Override
    public DepartmentDto getDepartmentById(Long id) {
        Department department = departmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Department not found with id: " + id));
        return departmentMapper.toDto(department);
    }

    @Override
    public DepartmentDto createDepartment(DepartmentDto departmentDto) {
        Department department = departmentMapper.toEntity(departmentDto);
        Department savedDepartment = departmentRepository.save(department);
        return departmentMapper.toDto(savedDepartment);
    }

    @Override
    public DepartmentDto updateDepartment(Long id, DepartmentDto updatedDepartmentDto) {
        Department existingDepartment = departmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Department not found with id: " + id));
        Department updatedDepartment = departmentMapper.toEntity(updatedDepartmentDto);
        updatedDepartment.setId(existingDepartment.getId()); // Сохраняем ID существующего отдела
        Department savedDepartment = departmentRepository.save(updatedDepartment);
        return departmentMapper.toDto(savedDepartment);
    }

    @Override
    public void deleteDepartment(Long id) {
        departmentRepository.deleteById(id);
    }
}