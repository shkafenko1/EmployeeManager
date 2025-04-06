package by.koronatech.office.core.repository;

import by.koronatech.office.core.model.Employee;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    // Corrected method name to query departments' IDs
    Page<Employee> findEmployeesByDepartments_Id(long id, Pageable pageable);
}