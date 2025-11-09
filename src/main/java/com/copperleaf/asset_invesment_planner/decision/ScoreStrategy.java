package com.copperleaf.asset_invesment_planner.decision;

import com.copperleaf.asset_invesment_planner.dto.ProjectPriorityResponse;

public interface ScoreStrategy {

    double score(ProjectPriorityResponse kpi, double wRisk, double wHealth, double wRoi, double wUtil);
}
