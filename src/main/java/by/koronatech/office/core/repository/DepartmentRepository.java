package by.koronatech.office.core.repository;

import by.koronatech.office.core.model.Department;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface DepartmentRepository extends JpaRepository<Department, Long> {
    boolean existsByName(String name);

    Optional<Department> findByName(String name);

    List<Department> findByCompanyId(Long companyId);

    @Modifying
    @Transactional
    @Query(nativeQuery = true, value = "DELETE FROM employee_department WHERE department_id = :departmentId")
    void deleteEmployeeDepartmentByDepartmentId(@Param("departmentId") Long departmentId);
}