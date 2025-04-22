package by.koronatech.office.api.dto;

import java.math.BigDecimal;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
@Builder
public class EmployeeDto {

    private Long id;
    private String name;
    private BigDecimal salary;
    private List<String> departmentNames;
    private boolean manager;
}