package com.copperleaf.asset_invesment_planner.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Data
public class AssetPrioritizeRequest {
    private Double weightHealth = 0.5;
    private Double weightROI    = 0.3;
    private Double weightCost   = 0.2;
    private Integer topN        = 100;
    private Boolean useForecast = false;
    private Double alpha        = 0.5;
}
