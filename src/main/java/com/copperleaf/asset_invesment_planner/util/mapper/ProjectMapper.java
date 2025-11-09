package com.copperleaf.asset_invesment_planner.util.mapper;

import com.copperleaf.asset_invesment_planner.dto.ProjectCreateRequest;
import com.copperleaf.asset_invesment_planner.dto.ProjectResponse;
import com.copperleaf.asset_invesment_planner.dto.ProjectUpdateRequest;
import com.copperleaf.asset_invesment_planner.entity.Project;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.stereotype.Component;

@Component
public class ProjectMapper {

    public Project toEntity(ProjectCreateRequest req) {
        Project p = new Project();
        p.setCode(req.getCode());
        p.setName(req.getName());
        p.setDescription(req.getDescription());
        p.setStatus(req.getStatus());
        p.setApprovedBudget(req.getApprovedBudget());
        p.setCurrency(req.getCurrency());
        p.setStartDate(req.getStartDate());
        p.setEndDate(req.getEndDate());
        p.setRiskScore(req.getRiskScore());
        return p;
    }

    public void mapUpdates(ProjectUpdateRequest req, Project p) {
        p.setName(req.getName());
        p.setDescription(req.getDescription());
        p.setStatus(req.getStatus());
        p.setApprovedBudget(req.getApprovedBudget());
        p.setCurrency(req.getCurrency());
        p.setStartDate(req.getStartDate());
        p.setEndDate(req.getEndDate());
        p.setRiskScore(req.getRiskScore());
    }

    public ProjectResponse toResponse(Project p) {
        ProjectResponse r = new ProjectResponse();
        r.setId(p.getId());
        r.setCode(p.getCode());
        r.setName(p.getName());
        r.setDescription(p.getDescription());
        r.setStatus(p.getStatus());
        r.setApprovedBudget(p.getApprovedBudget());
        r.setCurrency(p.getCurrency());
        r.setStartDate(p.getStartDate());
        r.setEndDate(p.getEndDate());
        r.setRiskScore(p.getRiskScore());
        r.setAssetCount(p.getAssets() == null ? 0 : p.getAssets().size());
        return r;
    }


}
