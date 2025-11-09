package com.copperleaf.asset_invesment_planner.dto;


import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Data
public class OptimizePortfolioResponse {
    private BigDecimal budgetCap;
    private BigDecimal totalSelectedBudget;
    private double totalScore;
    private List<ProjectPriorityResponse> selectedProjects;
}
