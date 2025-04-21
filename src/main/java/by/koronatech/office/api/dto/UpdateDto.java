package by.koronatech.office.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

import lombok.*;


@Setter
@Getter
@EqualsAndHashCode
@NoArgsConstructor
public class UpdateDto {

    @NotBlank(message = "Name cannot be empty")
    @Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
    private String name;

    @NotNull(message = "Salary cannot be null")
    @Positive(message = "Salary must be positive")
    private BigDecimal salary;

    private boolean manager = false; // Default value
}