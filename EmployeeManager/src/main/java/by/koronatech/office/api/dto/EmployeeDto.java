package by.koronatech.office.api.dto;

import java.math.BigDecimal;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@EqualsAndHashCode
@Builder
public class EmployeeDto {
    private int id;
    private String name;
    private BigDecimal salary;
    private String departmentName;
    private boolean manager;

    public boolean getManager() {
        return manager;
    }
}
