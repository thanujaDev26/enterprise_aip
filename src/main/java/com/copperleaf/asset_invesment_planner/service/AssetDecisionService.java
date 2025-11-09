package com.copperleaf.asset_invesment_planner.service;

import com.copperleaf.asset_invesment_planner.dto.AssetOptimizeRequest;
import com.copperleaf.asset_invesment_planner.dto.AssetOptimizeResponse;
import com.copperleaf.asset_invesment_planner.dto.AssetPrioritizeRequest;
import com.copperleaf.asset_invesment_planner.dto.AssetPriorityResponse;

import java.util.List;

public interface AssetDecisionService {

    List<AssetPriorityResponse> prioritize(String projectCode, AssetPrioritizeRequest dto);

    AssetOptimizeResponse optimize(AssetOptimizeRequest dto);

}
