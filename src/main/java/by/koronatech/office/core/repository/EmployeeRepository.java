package by.koronatech.office.core.repository;

import by.koronatech.office.core.model.Employee;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    Page<Employee> findByEmployeeDepartmentsDepartmentName(
            String departmentName,
            Pageable pageable
    );
}