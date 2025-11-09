package com.copperleaf.asset_invesment_planner.controller;


import com.copperleaf.asset_invesment_planner.dto.AssetOptimizeRequest;
import com.copperleaf.asset_invesment_planner.dto.AssetOptimizeResponse;
import com.copperleaf.asset_invesment_planner.dto.AssetPrioritizeRequest;
import com.copperleaf.asset_invesment_planner.dto.AssetPriorityResponse;
import com.copperleaf.asset_invesment_planner.service.AssetDecisionService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/decision/assets")
public class AssetDecisionController {

    private final AssetDecisionService assetDecisionService;

    @Autowired
    public AssetDecisionController(AssetDecisionService assetDecisionService) {
        this.assetDecisionService = assetDecisionService;
    }

    @PostMapping("/prioritize")
    public ResponseEntity<List<AssetPriorityResponse>> prioritizeAll(@Valid @RequestBody AssetPrioritizeRequest dto) {
        return ResponseEntity.ok(assetDecisionService.prioritize(null, dto));
    }

    @PostMapping("/prioritize/{projectCode}")
    public ResponseEntity<List<AssetPriorityResponse>> prioritizeForProject(
            @PathVariable String projectCode, @Valid @RequestBody AssetPrioritizeRequest dto
    ){
        return ResponseEntity.ok(assetDecisionService.prioritize(projectCode, dto));
    }

    @PostMapping("/optimize")
    public ResponseEntity<AssetOptimizeResponse> optimize(@Valid @RequestBody AssetOptimizeRequest dto){
        return ResponseEntity.ok(assetDecisionService.optimize(dto));
    }
}
