package com.copperleaf.asset_invesment_planner.dto;


import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Data
public class ProjectPriorityResponse {

    private String projectCode;
    private String projectName;
    private BigDecimal approvedBudget;
    private double score;
    private double risk;
    private double avgHealth;
    private double roi;
    private double utilization;
    private int assetCount;

}
