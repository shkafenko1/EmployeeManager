package by.koronatech.office.core.repository;

import by.koronatech.office.core.model.Company;
import by.koronatech.office.core.model.Employee;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CompanyRepository extends JpaRepository<Company, Long> {
    boolean existsByName(String name);

    @Query("SELECT DISTINCT e FROM Company c "
            + "JOIN c.departments d "
            + "JOIN d.employeeDepartments ed "
            + "JOIN ed.employee e "
            + "WHERE c.id = :companyId AND d.name = :departmentName")
    List<Employee> findEmployeesByDepartmentName(@Param("companyId") Long companyId,
                                                 @Param("departmentName") String departmentName);

    // Native Query: Найти компании с сотрудниками, у которых зарплата выше заданной
    @Query(nativeQuery = true,
            value = "SELECT DISTINCT c.* FROM company c "
                    + "JOIN department d ON d.company_id = c.id "
                    + "JOIN employee_department ed ON ed.department_id = d.id "
                    + "JOIN employee e ON e.id = ed.employee_id "
                    + "WHERE e.salary > :salary")
    List<Company> findCompaniesWithHighSalaryEmployeesNative(@Param("salary") BigDecimal salary);
    
    Optional<Company> findByName(String name);
}
