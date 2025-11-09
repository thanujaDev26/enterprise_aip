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
import com.copperleaf.asset_invesment_planner.service.ForecastService;
import org.springframework.stereotype.Service;


import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;


@Service
public class DecisionServiceImpl implements DecisionService {

    private final ProjectRepository projectRepository;
    private final ScoreStrategy scoreStrategy = new WeightedLinearScore();
    private final ForecastService forecastService;


    public DecisionServiceImpl(ProjectRepository projectRepository, ForecastService forecastService) {
        this.projectRepository = projectRepository;
        this.forecastService = forecastService;
    }

    private double nz(Double d, double def){ return d == null ? def : d; }
    private BigDecimal nz(BigDecimal v){ return v == null ? BigDecimal.ZERO : v; }
    private double nzNum(Integer v){ return v == null ? 0 : v; }

    private ProjectPriorityResponse computeKpi(Project p, boolean useForecast, double alpha) {
        ProjectPriorityResponse r = new ProjectPriorityResponse();
        r.setProjectCode(p.getCode());
        r.setProjectName(p.getName());
        r.setApprovedBudget(nz(p.getApprovedBudget()));
        r.setRisk(nzNum(p.getRiskScore()));

        var assets = p.getAssets() == null ? List.<Asset>of() : p.getAssets();
        r.setAssetCount(assets.size());

        double avgHealth = assets.isEmpty() ? 100.0 :
                assets.stream().mapToInt(a -> a.getHealthIndex() == null ? 100 : a.getHealthIndex()).average().orElse(100.0);
        r.setAvgHealth(avgHealth);

        double sumInitial = assets.stream().map(a -> a.getInitialInvestment()==null?0.0:a.getInitialInvestment()).mapToDouble(Double::doubleValue).sum();
        double roi;
        if (useForecast) {
            double sumForecast = assets.stream().map(a -> {
                var f = forecastService.forecastNextCurrentValue(a, alpha);
                return f == null ? a.getCurrentValue() : f;
            }).mapToDouble(bd -> bd == null ? 0.0 : bd.doubleValue()).sum();
            roi = sumInitial <= 0 ? 0.0 : (sumForecast / sumInitial);
        } else {
            var sumCurrent = assets.stream().map(a -> a.getCurrentValue()==null?java.math.BigDecimal.ZERO:a.getCurrentValue())
                    .reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add);
            roi = sumInitial <= 0 ? 0.0 : sumCurrent.doubleValue() / sumInitial;
        }
        r.setRoi(roi);

        var sumReplacement = assets.stream().map(a -> a.getReplacementCost()==null?java.math.BigDecimal.ZERO:a.getReplacementCost())
                .reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add);
        double util = r.getApprovedBudget().compareTo(java.math.BigDecimal.ZERO)==0?0.0:
                sumReplacement.divide(r.getApprovedBudget(), 6, java.math.RoundingMode.HALF_UP).doubleValue();
        r.setUtilization(util);

        return r;
    }


    private java.util.List<ProjectPriorityResponse> knapsackProjects(
            java.util.List<ProjectPriorityResponse> items, java.math.BigDecimal remaining) {

        java.math.BigDecimal unit = new java.math.BigDecimal("100000");
        int capacity = remaining.divide(unit, 0, java.math.RoundingMode.DOWN).intValue();
        int n = items.size();
        int[] w = items.stream().map(b -> b.getApprovedBudget().divide(unit, 0, java.math.RoundingMode.UP).intValue()).mapToInt(Integer::intValue).toArray();
        double[] v = items.stream().mapToDouble(ProjectPriorityResponse::getScore).toArray();

        double[][] dp = new double[n+1][capacity+1];
        boolean[][] take = new boolean[n+1][capacity+1];
        for (int i=1;i<=n;i++){
            int wi = w[i-1]; double vi = v[i-1];
            for (int c=0;c<=capacity;c++){
                double no = dp[i-1][c], yes = (wi<=c) ? dp[i-1][c-wi] + vi : -1;
                if (yes > no){ dp[i][c]=yes; take[i][c]=true; } else { dp[i][c]=no; }
            }
        }
        int c = capacity;
        java.util.List<ProjectPriorityResponse> picked = new java.util.ArrayList<>();
        for (int i=n;i>=1;i--){
            if (take[i][c]) { picked.add(items.get(i-1)); c -= w[i-1]; }
        }
        java.util.Collections.reverse(picked);
        return picked;
    }

    private java.util.List<ProjectPriorityResponse> enforceProjectDependencies(
            java.util.List<ProjectPriorityResponse> picked,
            java.util.Map<String, ProjectPriorityResponse> universe,
            java.util.Map<String, java.util.List<String>> depends,
            java.math.BigDecimal cap) {

        var set = new java.util.LinkedHashMap<String, ProjectPriorityResponse>();
        for (var p : picked) set.put(p.getProjectCode(), p);

        java.math.BigDecimal current = picked.stream().map(ProjectPriorityResponse::getApprovedBudget)
                .reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add);

        boolean changed;
        do {
            changed = false;
            for (var entry : new java.util.ArrayList<>(set.values())) {
                var requires = depends.getOrDefault(entry.getProjectCode(), java.util.List.of());
                for (String dep : requires) {
                    if (!set.containsKey(dep) && universe.containsKey(dep)) {
                        var cand = universe.get(dep);
                        if (current.add(cand.getApprovedBudget()).compareTo(cap) <= 0) {
                            set.put(dep, cand); current = current.add(cand.getApprovedBudget()); changed = true;
                        } else {
                            current = current.subtract(entry.getApprovedBudget());
                            set.remove(entry.getProjectCode());
                            changed = true;
                            break;
                        }
                    }
                }
            }
        } while (changed);

        return new java.util.ArrayList<>(set.values());
    }


    @Override
    public List<ProjectPriorityResponse> prioritize(PrioritizeRequest dto) {
        double wRisk = nz(dto.getWeightRisk(), 0.4);
        double wHealth = nz(dto.getWeightHealth(), 0.2);
        double wRoi = nz(dto.getWeightROI(), 0.3);
        double wUtil = nz(dto.getWeightUtil(), 0.1);

        List<Project> projects = projectRepository.findAll();

        List<ProjectPriorityResponse> scored = projects.stream()
                .map(p -> computeKpi(p, false, 0.5))
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
        boolean useForecast = Boolean.TRUE.equals(req.getUseForecast());
        double alpha = req.getAlpha() == null ? 0.5 : req.getAlpha();


        var all = projectRepository.findAll().stream()
                .map(p -> computeKpi(p, useForecast, alpha))
                .toList();

        var excluded = new java.util.HashSet<>(req.getMustExclude()==null?java.util.List.<String>of():req.getMustExclude());
        var items = all.stream().filter(k -> !excluded.contains(k.getProjectCode())).toList();

        for (var k : items)
            k.setScore(scoreStrategy.score(k, wRisk, wHealth, wRoi, wUtil));

        var byCode = items.stream().collect(java.util.stream.Collectors.toMap(ProjectPriorityResponse::getProjectCode, x->x));

        java.util.List<ProjectPriorityResponse> picked = new java.util.ArrayList<>();
        java.math.BigDecimal cap = req.getBudgetCap()==null?java.math.BigDecimal.ZERO:req.getBudgetCap();
        java.math.BigDecimal remaining = cap;

        var must = req.getMustInclude()==null?java.util.List.<String>of():req.getMustInclude();
        for (String code : must) {
            var k = byCode.get(code);
            if (k != null && k.getApprovedBudget().compareTo(remaining) <= 0) {
                picked.add(k);
                remaining = remaining.subtract(k.getApprovedBudget());
            }
        }

        java.util.Map<String, java.util.List<String>> depends = req.getDependsOn()==null?java.util.Map.of():req.getDependsOn();

        var pickedCodes = picked.stream()
                .map(ProjectPriorityResponse::getProjectCode)
                .collect(java.util.stream.Collectors.toList());

        var rest = items.stream()
                .filter(k -> !pickedCodes.contains(k.getProjectCode()))
                .toList();

        var bundle = knapsackProjects(rest, remaining);
        picked.addAll(bundle);

        picked = enforceProjectDependencies(picked, byCode, depends, cap);

        var resp = new OptimizePortfolioResponse();
        resp.setBudgetCap(cap);
        var total = picked.stream().map(ProjectPriorityResponse::getApprovedBudget)
                .reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add);
        resp.setTotalSelectedBudget(total);
        resp.setTotalScore(picked.stream().mapToDouble(ProjectPriorityResponse::getScore).sum());
        resp.setSelectedProjects(picked);
        return resp;
    }

}
