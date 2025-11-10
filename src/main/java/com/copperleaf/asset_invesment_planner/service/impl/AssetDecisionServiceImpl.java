package com.copperleaf.asset_invesment_planner.service.impl;


import com.copperleaf.asset_invesment_planner.dto.AssetOptimizeRequest;
import com.copperleaf.asset_invesment_planner.dto.AssetOptimizeResponse;
import com.copperleaf.asset_invesment_planner.dto.AssetPrioritizeRequest;
import com.copperleaf.asset_invesment_planner.dto.AssetPriorityResponse;
import com.copperleaf.asset_invesment_planner.entity.Asset;
import com.copperleaf.asset_invesment_planner.repository.AssetRepository;
import com.copperleaf.asset_invesment_planner.service.AssetDecisionService;
import com.copperleaf.asset_invesment_planner.service.ForecastService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class AssetDecisionServiceImpl implements AssetDecisionService {

    private final AssetRepository assetRepository;
    private final ForecastService forecastService;

    @Autowired
    public AssetDecisionServiceImpl(AssetRepository assetRepository, ForecastService forecastService) {
        this.assetRepository = assetRepository;
        this.forecastService = forecastService;
    }

    @Override
    public List<AssetPriorityResponse> prioritize(String projectCode, AssetPrioritizeRequest req) {
        var universe = (projectCode == null || projectCode.isBlank())
                ? assetRepository.findAll()
                : assetRepository.findByProject_Code(projectCode, org.springframework.data.domain.Pageable.unpaged()).getContent();

        double wH = nz(req.getWeightHealth(), 0.5);
        double wR = nz(req.getWeightROI(),    0.3);
        double wC = nz(req.getWeightCost(),   0.2);
        boolean useForecast = Boolean.TRUE.equals(req.getUseForecast());
        double alpha = req.getAlpha() == null ? 0.5 : req.getAlpha();

        return universe.stream()
                .map(a -> toKpi(a, useForecast, alpha))
                .peek(a -> a.setScore(score(a, wH, wR, wC)))
                .sorted(Comparator.comparingDouble(AssetPriorityResponse::getScore).reversed())
                .limit(req.getTopN() == null ? 100 : req.getTopN())
                .collect(Collectors.toList());
    }

    @Override
    public AssetOptimizeResponse optimize(AssetOptimizeRequest req) {
        var base = prioritize(req.getProjectCode(), toPrioritize(req));

        BigDecimal cap = req.getBudgetCap() == null ? BigDecimal.ZERO : req.getBudgetCap();

        var excl = new HashSet<>(req.getMustExcludeIds() == null ? List.<Long>of() : req.getMustExcludeIds());
        var items = base.stream().filter(a -> !excl.contains(a.getId())).collect(Collectors.toList());

        var byId = items.stream().collect(Collectors.toMap(AssetPriorityResponse::getId, x -> x));

        List<AssetPriorityResponse> picked = new ArrayList<>();
        BigDecimal remaining = cap;
        for (Long id : req.getMustIncludeIds() == null ? List.<Long>of() : req.getMustIncludeIds()) {
            var k = byId.get(id);
            if (k != null && k.getReplacementCost().compareTo(remaining) <= 0) {
                picked.add(k);
                remaining = remaining.subtract(k.getReplacementCost());
            }
        }

        var pickedIds = picked.stream().map(AssetPriorityResponse::getId).collect(Collectors.toSet());
        var rest = items.stream().filter(a -> !pickedIds.contains(a.getId())).collect(Collectors.toList());

        var bundle = knapsack(rest, remaining);
        picked.addAll(bundle);

        var deps = req.getDependsOn() == null ? Map.<Long, List<Long>>of() : req.getDependsOn();
        picked = enforceDependencies(picked, byId, deps, cap);

        var resp = new AssetOptimizeResponse();
        resp.setBudgetCap(cap);
        var total = picked.stream().map(AssetPriorityResponse::getReplacementCost)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        resp.setTotalSelectedCost(total);
        resp.setTotalScore(picked.stream().mapToDouble(AssetPriorityResponse::getScore).sum());
        resp.setSelectedAssets(picked);
        return resp;
    }

    private AssetPriorityResponse toKpi(Asset a, boolean useForecast, double alpha) {
        AssetPriorityResponse r = new AssetPriorityResponse();
        r.setId(a.getId());
        r.setName(a.getName());
        r.setType(a.getType());
        r.setProjectCode(a.getProject() != null ? a.getProject().getCode() : null);
        r.setHealthIndex(a.getHealthIndex());
        r.setInitialInvestment(a.getInitialInvestment());
        r.setCurrentValue(a.getCurrentValue());
        r.setReplacementCost(a.getReplacementCost());

        double init = a.getInitialInvestment() == null ? 0.0 : a.getInitialInvestment();
        double curr = a.getCurrentValue() == null ? 0.0 : a.getCurrentValue().doubleValue();
        if (useForecast) {
            var f = forecastService != null ? forecastService.forecastNextCurrentValue(a, alpha) : null;
            curr = (f == null ? curr : f.doubleValue());
        }
        r.setRoi(init <= 0 ? 0.0 : curr / init);
        return r;
    }

    private double score(AssetPriorityResponse a, double wH, double wR, double wC) {
        double hN = 1.0 - clamp01((a.getHealthIndex() == null ? 100 : a.getHealthIndex()) / 100.0);
        double roiN = clamp01(a.getRoi() / 2.0);
        double costN = 1.0 - clamp01(a.getReplacementCost().doubleValue() / 2_000_000d); // normalize by 2M
        return wH * hN + wR * roiN + wC * costN;
    }


    private List<AssetPriorityResponse> knapsack(List<AssetPriorityResponse> items, BigDecimal remaining) {
        BigDecimal unit = new BigDecimal("100000"); // 100k buckets
        int capacity = remaining.divide(unit, 0, RoundingMode.DOWN).intValue();
        int n = items.size();
        int[] w = items.stream().map(a -> a.getReplacementCost().divide(unit, 0, RoundingMode.UP).intValue())
                .mapToInt(Integer::intValue).toArray();
        double[] v = items.stream().mapToDouble(AssetPriorityResponse::getScore).toArray();

        double[][] dp = new double[n + 1][capacity + 1];
        boolean[][] take = new boolean[n + 1][capacity + 1];
        for (int i = 1; i <= n; i++) {
            int wi = w[i - 1]; double vi = v[i - 1];
            for (int c = 0; c <= capacity; c++) {
                double no = dp[i - 1][c], yes = (wi <= c) ? dp[i - 1][c - wi] + vi : -1;
                if (yes > no) { dp[i][c] = yes; take[i][c] = true; }
                else { dp[i][c] = no; }
            }
        }
        int c = capacity;
        List<AssetPriorityResponse> picked = new ArrayList<>();
        for (int i = n; i >= 1; i--) {
            if (take[i][c]) { picked.add(items.get(i - 1)); c -= w[i - 1]; }
        }
        Collections.reverse(picked);
        return picked;
    }


    private List<AssetPriorityResponse> enforceDependencies(
            List<AssetPriorityResponse> picked,
            Map<Long, AssetPriorityResponse> universe,
            Map<Long, List<Long>> depends,
            BigDecimal cap) {

        var set = new LinkedHashMap<Long, AssetPriorityResponse>();
        for (var p : picked) set.put(p.getId(), p);

        BigDecimal current = picked.stream().map(AssetPriorityResponse::getReplacementCost)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        boolean changed;
        do {
            changed = false;
            for (var entry : new ArrayList<>(set.values())) {
                var requires = depends.getOrDefault(entry.getId(), List.of());
                for (Long dep : requires) {
                    if (!set.containsKey(dep) && universe.containsKey(dep)) {
                        var cand = universe.get(dep);
                        if (current.add(cand.getReplacementCost()).compareTo(cap) <= 0) {
                            set.put(dep, cand); current = current.add(cand.getReplacementCost()); changed = true;
                        } else {
                            // cannot meet dependency; drop parent
                            current = current.subtract(entry.getReplacementCost());
                            set.remove(entry.getId());
                            changed = true;
                            break;
                        }
                    }
                }
            }
        } while (changed);

        return new ArrayList<>(set.values());
    }


    private double nz(Double d, double def){ return d == null ? def : d; }
    private double clamp01(double v){ return v < 0 ? 0 : (v > 1 ? 1 : v); }

    private AssetPrioritizeRequest toPrioritize(AssetOptimizeRequest r){
        var p = new AssetPrioritizeRequest();
        p.setWeightHealth(r.getWeightHealth());
        p.setWeightROI(r.getWeightROI());
        p.setWeightCost(r.getWeightCost());
        p.setUseForecast(r.getUseForecast());
        p.setAlpha(r.getAlpha());
        p.setTopN(10_000);
        return p;
    }


}
