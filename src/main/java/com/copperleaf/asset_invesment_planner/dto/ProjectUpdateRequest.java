package com.copperleaf.asset_invesment_planner.dto;


import com.copperleaf.asset_invesment_planner.entity.enums.Currency;
import com.copperleaf.asset_invesment_planner.entity.enums.ProjectStatus;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProjectUpdateRequest {

    @NotBlank
    @Size(max = 200)
    private String name;

    @Size(max = 2000)
    private String description;

    @NotNull
    private ProjectStatus status;

    @NotNull
    @DecimalMin("0.00")
    private BigDecimal approvedBudget;

    @NotNull
    private Currency currency;

    private LocalDate StartDate;
    private LocalDate EndDate;

    @Min(0)
    @Max(100)
    private Integer riskScore;
}
