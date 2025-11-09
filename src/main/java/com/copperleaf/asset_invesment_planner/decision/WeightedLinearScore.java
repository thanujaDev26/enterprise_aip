package com.copperleaf.asset_invesment_planner.decision;

import com.copperleaf.asset_invesment_planner.dto.ProjectPriorityResponse;

public class WeightedLinearScore implements ScoreStrategy {

    @Override
    public double score(ProjectPriorityResponse kpi, double wRisk, double wHealth, double wRoi, double wUtil) {
        double riskN = 1.0 -clamp01(kpi.getRisk() / 100.0);
        double health = 1.0 - clamp01(kpi.getAvgHealth() / 100.0);
        double roiN = clamp01(kpi.getRoi() / 2.0);
        double utilN = clamp01(kpi.getUtilization() / 2.0);

        return wRisk * riskN + wHealth * health + wRoi * roiN + wUtil * utilN;
    }

    private double clamp01(double v){
        return v < 0 ? 0 : (v > 1 ? 1 : v);
    }
}
