package by.koronatech.office.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class CreateEmployeeDto {

    @NotBlank(message = "Name cannot be empty")
    @Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
    private String name;

    @NotNull(message = "Salary cannot be null")
    @Positive(message = "Salary must be positive")
    private BigDecimal salary;

    @Size(max = 5, message = "Cannot have more than 5 departments")
    private List<@NotBlank(message = "Department name cannot be empty") String> departmentNames;

    private boolean manager = false; // Default value
}