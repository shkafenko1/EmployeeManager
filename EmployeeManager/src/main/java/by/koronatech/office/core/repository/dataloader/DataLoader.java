package by.koronatech.office.core.repository.dataloader;

import by.koronatech.office.core.Department;
import by.koronatech.office.core.Employee;
import by.koronatech.office.core.repository.DepartmentRepository;
import by.koronatech.office.core.repository.EmployeeRepository;
import java.math.BigDecimal;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataLoader implements CommandLineRunner {

    private final DepartmentRepository departmentRepository;
    private final EmployeeRepository employeeRepository;

    public DataLoader(DepartmentRepository departmentRepository,
                      EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
        this.departmentRepository = departmentRepository;
    }

    @Override
    public void run(String... args) {
        Department itDepartment = new Department(1, "IT Department");
        Department hrDepartment = new Department(2, "HR Department");

        departmentRepository.addDepartment(itDepartment);
        departmentRepository.addDepartment(hrDepartment);

        Employee emp1 = new Employee(1, "Maksim", new BigDecimal(5432), itDepartment, true);
        Employee emp2 = new Employee(2, "Egor", new BigDecimal(1000), itDepartment, false);
        Employee emp3 = new Employee(3, "Tanya", new BigDecimal(3000), hrDepartment, true);

        employeeRepository.addEmployee(emp1);
        employeeRepository.addEmployee(emp2);
        employeeRepository.addEmployee(emp3);
    }
}
