package com.copperleaf.asset_invesment_planner.controller;

import com.copperleaf.asset_invesment_planner.dto.OptimizePortfolioRequest;
import com.copperleaf.asset_invesment_planner.dto.OptimizePortfolioResponse;
import com.copperleaf.asset_invesment_planner.dto.PrioritizeRequest;
import com.copperleaf.asset_invesment_planner.dto.ProjectPriorityResponse;
import com.copperleaf.asset_invesment_planner.service.DecisionService;
import com.copperleaf.asset_invesment_planner.util.StandardResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/v1/decisions")
public class DecisionController {

    private DecisionService decisionService;

    @Autowired
    public DecisionController(DecisionService decisionService) {
        this.decisionService = decisionService;
    }

    @PostMapping("/prioritize")
    public ResponseEntity<StandardResponse> prioritize(@Valid @RequestBody PrioritizeRequest dto) {
//        return ResponseEntity.ok(decisionService.prioritize(dto));
        return new ResponseEntity<>(
                new StandardResponse(
                        200, "Decision has been prioritized and sent back to the server",
                        decisionService.prioritize(dto)
                ), HttpStatus.OK
        );
    }

    @PostMapping("/optimize")
    public ResponseEntity<StandardResponse> optimize(@Valid @RequestBody OptimizePortfolioRequest dto){
//        return ResponseEntity.ok(decisionService.optimize(dto));
        return new ResponseEntity<>(
                new StandardResponse(
                        200, "Decision has been optimized and sent back to the server",
                        decisionService.optimize(dto)
                ), HttpStatus.OK
        );
    }
}
