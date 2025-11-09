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
public class ProjectCreateRequest {

    @NotBlank
    @Size(max = 64)
    private String code;

    @NotBlank
    @Size(max = 200)
    private String name;

    @Size(max = 2000)
    private String description;

    @NotNull
    private ProjectStatus status = ProjectStatus.DRAFT;

    @NotNull
    @DecimalMin("0.00")
    private BigDecimal approvedBudget =  BigDecimal.ZERO;

    @NotNull
    private Currency currency = Currency.USD;

    private LocalDate startDate;
    private LocalDate endDate;

    @Min(0)
    @Max(100)
    private Integer riskScore = 0;

}
