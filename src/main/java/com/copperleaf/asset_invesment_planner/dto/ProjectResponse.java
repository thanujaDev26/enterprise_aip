package com.copperleaf.asset_invesment_planner.dto;

import com.copperleaf.asset_invesment_planner.entity.enums.Currency;
import com.copperleaf.asset_invesment_planner.entity.enums.ProjectStatus;
import jakarta.validation.constraints.NotNull;
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
public class ProjectResponse {
    private Long id;
    private String code;
    private String name;
    private String description;
    private ProjectStatus status;
    private BigDecimal approvedBudget;
    private Currency currency;
    private LocalDate startDate;
    private LocalDate endDate;
    private Integer riskScore;
    private int assetCount;


}
