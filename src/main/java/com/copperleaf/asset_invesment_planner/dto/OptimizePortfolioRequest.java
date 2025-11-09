package com.copperleaf.asset_invesment_planner.dto;


import jakarta.validation.constraints.DecimalMin;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Data
public class OptimizePortfolioRequest {

    @DecimalMin("0.00")
    private BigDecimal budgetCap;
    private Double weightRisk   = 0.4;
    private Double weightHealth = 0.2;
    private Double weightROI    = 0.3;
    private Double weightUtil   = 0.1;
}
