package by.koronatech.office.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@Builder
public class DepartmentDto {

    @NotBlank(message = "Company name cannot be empty")
    @Size(min = 2, max = 100, message = "Company name must be between 2 and 100 characters")
    private String company;

    @NotBlank(message = "Department name cannot be empty")
    @Size(min = 2, max = 100, message = "Department name must be between 2 and 100 characters")
    private String name;
}