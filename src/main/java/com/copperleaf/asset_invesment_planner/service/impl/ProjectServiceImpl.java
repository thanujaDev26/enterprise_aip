package com.copperleaf.asset_invesment_planner.service.impl;

import com.copperleaf.asset_invesment_planner.dto.PageResponse;
import com.copperleaf.asset_invesment_planner.dto.ProjectCreateRequest;
import com.copperleaf.asset_invesment_planner.dto.ProjectResponse;
import com.copperleaf.asset_invesment_planner.dto.ProjectUpdateRequest;
import com.copperleaf.asset_invesment_planner.entity.Project;
import com.copperleaf.asset_invesment_planner.entity.enums.ProjectStatus;
import com.copperleaf.asset_invesment_planner.exception.ProjectNotFoundException;
import com.copperleaf.asset_invesment_planner.repository.ProjectRepository;
import com.copperleaf.asset_invesment_planner.service.ProjectService;
import com.copperleaf.asset_invesment_planner.util.mapper.ProjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class ProjectServiceImpl implements ProjectService {

    private final ProjectRepository projectRepository;
    private final ProjectMapper projectMapper;

    @Autowired
    public ProjectServiceImpl(ProjectRepository projectRepository, ProjectMapper projectMapper) {
        this.projectRepository = projectRepository;
        this.projectMapper = projectMapper;
    }

    @Override
    public ProjectResponse create(ProjectCreateRequest dto) {
        if(projectRepository.existsByCode(dto.getCode())){
            throw new ProjectNotFoundException("Project code already exists: " + dto.getCode());
        }
        Project project = projectMapper.toEntity(dto);
        projectRepository.save(project);
        return projectMapper.toResponse(project);
    }

    @Override
    public ProjectResponse update(String code, ProjectUpdateRequest dto) {
        Project project = projectRepository.findByCode(code).orElseThrow(() -> new ProjectNotFoundException("Project code not exists: " + code));
        projectMapper.mapUpdates(dto, project);
        projectRepository.save(project);
        return projectMapper.toResponse(project);
    }

    @Override
    public ProjectResponse getByCode(String code) {
        Project project =  projectRepository.findByCode(code).orElseThrow(() -> new ProjectNotFoundException("Project code not exists: " + code));
        return projectMapper.toResponse(project);
    }

    @Override
    public void deleteByCOde(String code) {
        Project project =  projectRepository.findByCode(code).orElseThrow(() -> new ProjectNotFoundException("Project code not exists: " + code));
        projectRepository.delete(project);
    }

    @Override
    public PageResponse<ProjectResponse> search(ProjectStatus status, Pageable pageable) {
        Page<Project> page = (status == null) ? projectRepository.findAll(pageable) : projectRepository.findByStatus(status, pageable);
        return new PageResponse<>(
                page.map(projectMapper::toResponse).getContent(), page.getNumber(), page.getSize(), page.getTotalElements(), page.getTotalPages()
        );
    }
}
