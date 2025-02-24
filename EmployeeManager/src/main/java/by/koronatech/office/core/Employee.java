package by.koronatech.office.core;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@Builder
@AllArgsConstructor
@ToString
public class Employee {
    private int id;
    private String name;
    private BigDecimal salary;
    private Department department;
    private boolean manager;

    public boolean getManager() {
        return manager;
    }
}
