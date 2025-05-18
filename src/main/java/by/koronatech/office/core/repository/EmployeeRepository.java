package by.koronatech.office.core.repository;

import by.koronatech.office.core.model.Employee;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    Page<Employee> findByEmployeeDepartmentsDepartmentName(
            String departmentName,
            Pageable pageable
    );

    @Query("SELECT DISTINCT e FROM Employee e JOIN e.employeeDepartments ed WHERE ed.department.id = :departmentId")
    List<Employee> findByDepartmentId(@Param("departmentId") Long departmentId);

    @Query(nativeQuery = true, value = "SELECT employee_id FROM employee_department WHERE department_id = :departmentId")
    List<Long> findEmployeeIdsByDepartmentId(@Param("departmentId") Long departmentId);
}