package com.copperleaf.asset_invesment_planner.dto;


import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Data
public class AssetResponse {

    private Long id;

    private String name;

    private String type;

    private BigDecimal replacementCost;

    private BigDecimal currentValue;

    private Integer healthIndex;

    private Double initialInvestment;

    private Double roi;

    private String projectCode;

}
