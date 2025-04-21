package by.koronatech.office.api.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

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