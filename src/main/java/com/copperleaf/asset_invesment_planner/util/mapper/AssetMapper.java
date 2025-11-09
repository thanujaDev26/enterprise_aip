package com.copperleaf.asset_invesment_planner.util.mapper;

import com.copperleaf.asset_invesment_planner.dto.AssetCreateRequest;
import com.copperleaf.asset_invesment_planner.dto.AssetResponse;
import com.copperleaf.asset_invesment_planner.dto.AssetUpdateRequest;
import com.copperleaf.asset_invesment_planner.entity.Asset;
import com.copperleaf.asset_invesment_planner.entity.Project;
import org.springframework.stereotype.Component;

@Component
public class AssetMapper {


    public Asset toEntity(AssetCreateRequest req, Project project) {
        Asset a = new Asset();
        a.setName(req.getName());
        a.setType(req.getType());
        a.setReplacementCost(req.getReplacementCost());
        a.setCurrentValue(req.getCurrentValue());
        a.setHealthIndex(req.getHealthIndex());
        a.setInitialInvestment(req.getInitialInvestment());
        a.setRoi(req.getRoi());
        a.setProject(project);
        return a;
    }


    public void mapUpdates(AssetUpdateRequest req, Asset a) {
        if (req.getName() != null) a.setName(req.getName());
        if (req.getType() != null) a.setType(req.getType());
        if (req.getReplacementCost() != null) a.setReplacementCost(req.getReplacementCost());
        if (req.getCurrentValue() != null) a.setCurrentValue(req.getCurrentValue());
        if (req.getHealthIndex() != null) a.setHealthIndex(req.getHealthIndex());


        if (hasInitialInvestment(req)) a.setInitialInvestment(req.getInitialInvestment());
        if (hasRoi(req)) a.setRoi(req.getRoi());
    }


    public AssetResponse toResponse(Asset a) {
        AssetResponse r = new AssetResponse();
        r.setId(a.getId());
        r.setName(a.getName());
        r.setType(a.getType());
        r.setReplacementCost(a.getReplacementCost());
        r.setCurrentValue(a.getCurrentValue());
        r.setHealthIndex(a.getHealthIndex());
        r.setInitialInvestment(a.getInitialInvestment());
        r.setRoi(a.getRoi());
        r.setProjectCode(a.getProject() != null ? a.getProject().getCode() : null);
        return r;
    }



    private boolean hasInitialInvestment(AssetUpdateRequest req) {
        try { return req.getInitialInvestment() != null; } catch (NoSuchMethodError e) { return false; }
    }

    private boolean hasRoi(AssetUpdateRequest req) {
        try { return req.getRoi() != null; } catch (NoSuchMethodError e) { return false; }
    }
}
