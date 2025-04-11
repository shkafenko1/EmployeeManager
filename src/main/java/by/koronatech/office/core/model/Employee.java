package by.koronatech.office.core.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Entity
public class Employee {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private BigDecimal salary;

    @Column(nullable = false)
    private boolean manager = false; // Initialize with default value

    @OneToMany(mappedBy = "employee", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private Set<EmployeeDepartment> employeeDepartments = new HashSet<>();  // Initialize here

    public void updateDepartments(Set<Department> newDepartments) {
        if (this.employeeDepartments == null) {
            this.employeeDepartments = new HashSet<>();
        }

        // Clear existing associations and sync the Department side
        for (EmployeeDepartment ed : new HashSet<>(this.employeeDepartments)) {
            ed.getDepartment().getEmployeeDepartments().remove(ed);
        }
        this.employeeDepartments.clear();

        // Add new associations
        if (newDepartments != null) {
            newDepartments.forEach(department -> {
                EmployeeDepartment ed = new EmployeeDepartment();
                ed.setEmployee(this);
                ed.setDepartment(department);
                this.employeeDepartments.add(ed);
                department.getEmployeeDepartments().add(ed); // Sync the Department side
            });
        }
    }
}