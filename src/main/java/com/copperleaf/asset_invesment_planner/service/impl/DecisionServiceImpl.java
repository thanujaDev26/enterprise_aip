package com.copperleaf.asset_invesment_planner.service.impl;

import com.copperleaf.asset_invesment_planner.decision.ScoreStrategy;
import com.copperleaf.asset_invesment_planner.decision.WeightedLinearScore;
import com.copperleaf.asset_invesment_planner.dto.OptimizePortfolioRequest;
import com.copperleaf.asset_invesment_planner.dto.OptimizePortfolioResponse;
import com.copperleaf.asset_invesment_planner.dto.PrioritizeRequest;
import com.copperleaf.asset_invesment_planner.dto.ProjectPriorityResponse;
import com.copperleaf.asset_invesment_planner.entity.Asset;
import com.copperleaf.asset_invesment_planner.entity.Project;
import com.copperleaf.asset_invesment_planner.repository.ProjectRepository;
import com.copperleaf.asset_invesment_planner.service.DecisionService;
import lombok.Setter;
import org.springframework.stereotype.Service;


import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;


@Service
public class DecisionServiceImpl implements DecisionService {

    private final ProjectRepository projectRepository;
    private final ScoreStrategy scoreStrategy = new WeightedLinearScore();


    public DecisionServiceImpl(ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }

    private double nz(Double d, double def){ return d == null ? def : d; }
    private BigDecimal nz(BigDecimal v){ return v == null ? BigDecimal.ZERO : v; }
    private double nzNum(Integer v){ return v == null ? 0 : v; }

    private ProjectPriorityResponse computeKpi(Project p) {
        ProjectPriorityResponse r = new ProjectPriorityResponse();
        r.setProjectCode(p.getCode());
        r.setProjectName(p.getName());
        r.setApprovedBudget(nz(p.getApprovedBudget()));
        r.setRisk(nzNum(p.getRiskScore())); // 0..100

        List<Asset> assets = p.getAssets() == null ? List.of() : p.getAssets();
        int count = assets.size();
        r.setAssetCount(count);

        // avg health
        double avgHealth = count == 0 ? 100.0 :
                assets.stream().mapToInt(a -> a.getHealthIndex() == null ? 100 : a.getHealthIndex()).average().orElse(100.0);
        r.setAvgHealth(avgHealth);

        // ROI ratio = sum(currentValue)/sum(initialInvestment) (fallback if zero)
        BigDecimal sumCurrent = assets.stream()
                .map(a -> a.getCurrentValue() == null ? BigDecimal.ZERO : a.getCurrentValue())
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        double sumInitial = assets.stream()
                .map(a -> a.getInitialInvestment() == null ? 0.0 : a.getInitialInvestment())
                .mapToDouble(Double::doubleValue).sum();
        double roi = sumInitial <= 0 ? 0.0 : sumCurrent.doubleValue() / sumInitial;
        r.setRoi(roi);

        // utilization = sum(replacementCost)/approvedBudget
        BigDecimal sumReplacement = assets.stream()
                .map(a -> a.getReplacementCost() == null ? BigDecimal.ZERO : a.getReplacementCost())
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        double util = r.getApprovedBudget().compareTo(BigDecimal.ZERO) == 0
                ? 0.0 : sumReplacement.divide(r.getApprovedBudget(), 6, RoundingMode.HALF_UP).doubleValue();
        r.setUtilization(util);

        return r;
    }

    private List<ProjectPriorityResponse> knapsack(List<ProjectPriorityResponse> items, int capacity, BigDecimal unit) {
        int n = items.size();
        double[][] dp = new double[n + 1][capacity + 1];
        boolean[][] take = new boolean[n + 1][capacity + 1];

        int[] weights = items.stream()
                .map(b -> b.getApprovedBudget().divide(unit, 0, RoundingMode.UP).intValue())
                .mapToInt(Integer::intValue).toArray();
        double[] values = items.stream().mapToDouble(ProjectPriorityResponse::getScore).toArray();

        for (int i = 1; i <= n; i++) {
            int w = weights[i - 1];
            double val = values[i - 1];
            for (int c = 0; c <= capacity; c++) {
                double notake = dp[i - 1][c];
                double takeVal = -1;
                if (w <= c) takeVal = dp[i - 1][c - w] + val;
                if (takeVal > notake) {
                    dp[i][c] = takeVal;
                    take[i][c] = true;
                } else {
                    dp[i][c] = notake;
                    take[i][c] = false;
                }
            }
        }
        // reconstruct
        int c = capacity;
        List<ProjectPriorityResponse> selected = new ArrayList<>();
        for (int i = n; i >= 1; i--) {
            if (take[i][c]) {
                selected.add(items.get(i - 1));
                c -= weights[i - 1];
            }
        }
        Collections.reverse(selected);
        return selected;
    }

    @Override
    public List<ProjectPriorityResponse> prioritize(PrioritizeRequest dto) {
        double wRisk = nz(dto.getWeightRisk(), 0.4);
        double wHealth = nz(dto.getWeightHealth(), 0.2);
        double wRoi = nz(dto.getWeightROI(), 0.3);
        double wUtil = nz(dto.getWeightUtil(), 0.1);

        List<Project> projects = projectRepository.findAll();

        List<ProjectPriorityResponse> scored = projects.stream()
                .map(p -> computeKpi(p))
                .peek(kpi -> kpi.setScore(
                        scoreStrategy.score(kpi, wRisk,wHealth,wRoi,wUtil)))
                .sorted(Comparator.comparingDouble(ProjectPriorityResponse::getScore).reversed())
                .limit(dto.getTopN() != null ? dto.getTopN() : 50)
                .collect(Collectors.toList());

        return scored;
    }

    @Override
    public OptimizePortfolioResponse optimize(OptimizePortfolioRequest req) {
        double wRisk = nz(req.getWeightRisk(), 0.4);
        double wHealth = nz(req.getWeightHealth(), 0.2);
        double wRoi = nz(req.getWeightROI(), 0.3);
        double wUtil = nz(req.getWeightUtil(), 0.1);

        List<ProjectPriorityResponse> items = projectRepository.findAll().stream()
                .map(this::computeKpi)
                .peek(k -> k.setScore(scoreStrategy.score(k, wRisk, wHealth, wRoi, wUtil)))
                .collect(Collectors.toList());

        BigDecimal cap = req.getBudgetCap() == null ? BigDecimal.ZERO : req.getBudgetCap();

        // 0/1 knapsack by discretizing budget into units (e.g., 100k). Adjust granularity as needed.
        BigDecimal unit = new BigDecimal("100000"); // 100k

        int capacity = cap.divide(unit, 0, RoundingMode.DOWN).intValue();

        List<ProjectPriorityResponse> selected = knapsack(items, capacity, unit);

        OptimizePortfolioResponse resp = new OptimizePortfolioResponse();
        resp.setBudgetCap(cap);

        BigDecimal total = selected.stream()
                .map(ProjectPriorityResponse::getApprovedBudget)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        resp.setTotalSelectedBudget(total);

        double totalScore = selected.stream().mapToDouble(ProjectPriorityResponse::getScore).sum();

        resp.setTotalScore(totalScore);

        resp.setSelectedProjects(selected);
        return resp;

    }
}
