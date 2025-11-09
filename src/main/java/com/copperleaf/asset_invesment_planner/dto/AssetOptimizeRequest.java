package com.copperleaf.asset_invesment_planner.dto;

import jakarta.validation.constraints.DecimalMin;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Data
public class AssetOptimizeRequest {

    private String projectCode;
    @DecimalMin("0.00")
    private BigDecimal budgetCap;

    private Double weightHealth = 0.5;
    private Double weightROI    = 0.3;
    private Double weightCost   = 0.2;

    private List<Long> mustIncludeIds;
    private List<Long> mustExcludeIds;
    private Map<Long, List<Long>> dependsOn;

    private Boolean useForecast = false;
    private Double alpha = 0.5;
}
