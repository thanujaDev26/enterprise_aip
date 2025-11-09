package com.copperleaf.asset_invesment_planner.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Data
public class PrioritizeRequest {

    private Double weightRisk      = 0.4;
    private Double weightHealth    = 0.2;
    private Double weightROI       = 0.3;
    private Double weightUtil      = 0.1;
    @Min(1)
    @Max(200) private Integer topN = 50;
}
