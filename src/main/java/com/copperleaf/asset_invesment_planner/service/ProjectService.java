package com.copperleaf.asset_invesment_planner.service;


import com.copperleaf.asset_invesment_planner.dto.PageResponse;
import com.copperleaf.asset_invesment_planner.dto.ProjectCreateRequest;
import com.copperleaf.asset_invesment_planner.dto.ProjectResponse;
import com.copperleaf.asset_invesment_planner.dto.ProjectUpdateRequest;
import com.copperleaf.asset_invesment_planner.entity.enums.ProjectStatus;
import org.springframework.data.domain.Pageable;


public interface ProjectService {

    ProjectResponse create(ProjectCreateRequest dto);

    ProjectResponse update(String code, ProjectUpdateRequest dto);

    ProjectResponse getByCode(String code);

    void deleteByCOde(String code);

    PageResponse<ProjectResponse> search(ProjectStatus status, Pageable pageable);
}
