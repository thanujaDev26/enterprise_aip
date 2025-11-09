package com.copperleaf.asset_invesment_planner.controller;

import com.copperleaf.asset_invesment_planner.dto.OptimizePortfolioRequest;
import com.copperleaf.asset_invesment_planner.dto.OptimizePortfolioResponse;
import com.copperleaf.asset_invesment_planner.dto.PrioritizeRequest;
import com.copperleaf.asset_invesment_planner.dto.ProjectPriorityResponse;
import com.copperleaf.asset_invesment_planner.service.DecisionService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/decisions")
public class DecisionController {

    private DecisionService decisionService;

    @Autowired
    public DecisionController(DecisionService decisionService) {
        this.decisionService = decisionService;
    }

    @PostMapping("/prioritize")
    public ResponseEntity<List<ProjectPriorityResponse>> prioritize(@Valid @RequestBody PrioritizeRequest dto) {
        return ResponseEntity.ok(decisionService.prioritize(dto));
    }

    @PostMapping("/optimize")
    public ResponseEntity<OptimizePortfolioResponse> oprimize(@Valid @RequestBody OptimizePortfolioRequest dto){
        return ResponseEntity.ok(decisionService.optimize(dto));
    }
}
