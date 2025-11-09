package com.copperleaf.asset_invesment_planner.dto;


import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Data
public class AssetOptimizeResponse {
    private BigDecimal budgetCap;
    private BigDecimal totalSelectedCost;
    private double totalScore;
    private List<AssetPriorityResponse> selectedAssets;
}
