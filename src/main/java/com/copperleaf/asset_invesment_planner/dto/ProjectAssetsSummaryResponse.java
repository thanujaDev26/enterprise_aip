package com.copperleaf.asset_invesment_planner.dto;


import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Data
public class ProjectAssetsSummaryResponse {

    private String projectCode;

    private int assetCount;

    private BigDecimal totalReplacementCost;

    private BigDecimal totalCurrentValue;

    private double budgetUtilizationPct;
}
