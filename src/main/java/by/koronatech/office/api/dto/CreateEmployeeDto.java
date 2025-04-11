package by.koronatech.office.api.dto;

import java.math.BigDecimal;
import java.util.List;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@EqualsAndHashCode
@Builder
public class CreateEmployeeDto {
    private String name;
    private BigDecimal salary;
    private List<String> departmentNames;
    private boolean manager = false; // Default value
}