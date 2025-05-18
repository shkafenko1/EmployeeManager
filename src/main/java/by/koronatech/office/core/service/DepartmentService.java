package by.koronatech.office.core.service;

import by.koronatech.office.api.dto.DepartmentDto;
import by.koronatech.office.api.dto.DepartmentReturnDto;

import java.util.List;

public interface DepartmentService {
    List<DepartmentReturnDto> getAllDepartments();

    DepartmentReturnDto getDepartmentById(Long id);

    DepartmentDto createDepartment(DepartmentDto departmentDto);

    DepartmentDto updateDepartment(Long id, DepartmentDto updatedDepartmentDto);

    void deleteDepartment(Long id);
}
