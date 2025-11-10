package com.copperleaf.asset_invesment_planner.controller;


import com.copperleaf.asset_invesment_planner.dto.AssetOptimizeRequest;
import com.copperleaf.asset_invesment_planner.dto.AssetOptimizeResponse;
import com.copperleaf.asset_invesment_planner.dto.AssetPrioritizeRequest;
import com.copperleaf.asset_invesment_planner.dto.AssetPriorityResponse;
import com.copperleaf.asset_invesment_planner.service.AssetDecisionService;
import com.copperleaf.asset_invesment_planner.util.StandardResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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
    public ResponseEntity<StandardResponse> prioritizeAll(@Valid @RequestBody AssetPrioritizeRequest dto) {
//        return ResponseEntity.ok(assetDecisionService.prioritize(null, dto));
        return new ResponseEntity<>(
                new StandardResponse(
                        200, "Assets have been prioritized and sent back to the client",
                        assetDecisionService.prioritize(null, dto)
                ), HttpStatus.OK
        );
    }

    @PostMapping("/prioritize/{projectCode}")
    public ResponseEntity<StandardResponse> prioritizeForProject(
            @PathVariable String projectCode, @Valid @RequestBody AssetPrioritizeRequest dto
    ){
//        return ResponseEntity.ok(assetDecisionService.prioritize(projectCode, dto));
        return new ResponseEntity<>(
                new StandardResponse(
                        200, "Assets have been prioritized by projecrwise and sent back to the server",
                        assetDecisionService.prioritize(projectCode,dto)
                ), HttpStatus.OK
        );
    }

    @PostMapping("/optimize")
    public ResponseEntity<StandardResponse> optimize(@Valid @RequestBody AssetOptimizeRequest dto){
//        return ResponseEntity.ok(assetDecisionService.optimize(dto));
        return new ResponseEntity<>(
                new StandardResponse(
                        200, "Assets have been optimized",
                        assetDecisionService.optimize(dto)
                ), HttpStatus.OK
        );
    }
}
