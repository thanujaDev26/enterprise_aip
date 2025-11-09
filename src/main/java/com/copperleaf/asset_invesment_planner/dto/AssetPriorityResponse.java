package com.copperleaf.asset_invesment_planner.dto;


import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Data
public class AssetPriorityResponse {
    private Long id;
    private String name;
    private String type;
    private String projectCode;
    private Integer healthIndex;
    private Double initialInvestment;
    private BigDecimal currentValue;
    private BigDecimal replacementCost;
    private double roi;
    private double score;
}
