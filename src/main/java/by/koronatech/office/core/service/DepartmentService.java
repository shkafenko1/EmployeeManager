package by.koronatech.office.core.service;

import by.koronatech.office.api.dto.DepartmentDto;
import by.koronatech.office.core.mapper.DepartmentMapper;
import by.koronatech.office.core.repository.DepartmentRepository;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class DepartmentService implements BaseDepartmentService {

    private final DepartmentRepository departmentRepository;
    private final DepartmentMapper departmentMapper;

    @Override
    public List<DepartmentDto> getAllDepartments() {
        return departmentMapper
                .toDtos(departmentRepository.getDepartments().values().stream().toList());
    }
}
