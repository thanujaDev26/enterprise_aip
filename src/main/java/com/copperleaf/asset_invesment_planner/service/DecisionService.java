package com.copperleaf.asset_invesment_planner.service;

import com.copperleaf.asset_invesment_planner.dto.OptimizePortfolioRequest;
import com.copperleaf.asset_invesment_planner.dto.OptimizePortfolioResponse;
import com.copperleaf.asset_invesment_planner.dto.PrioritizeRequest;
import com.copperleaf.asset_invesment_planner.dto.ProjectPriorityResponse;

import java.util.List;


public interface DecisionService {

    List<ProjectPriorityResponse> prioritize(PrioritizeRequest dto);

    OptimizePortfolioResponse optimize(OptimizePortfolioRequest dto);
}
