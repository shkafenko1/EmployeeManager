package by.koronatech.office.api.dto;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;


@Setter
@Getter
@EqualsAndHashCode
@Builder
public class UpdateDto {
    private String name;
    private BigDecimal salary;
    private boolean manager = false; // Default value
}