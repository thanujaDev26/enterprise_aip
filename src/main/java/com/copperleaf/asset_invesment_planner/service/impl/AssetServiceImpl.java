package com.copperleaf.asset_invesment_planner.service.impl;

import com.copperleaf.asset_invesment_planner.dto.*;
import com.copperleaf.asset_invesment_planner.entity.Asset;
import com.copperleaf.asset_invesment_planner.entity.Project;
import com.copperleaf.asset_invesment_planner.exception.AssetNotFoundException;
import com.copperleaf.asset_invesment_planner.exception.ProjectNotFoundException;
import com.copperleaf.asset_invesment_planner.repository.AssetRepository;
import com.copperleaf.asset_invesment_planner.repository.ProjectRepository;
import com.copperleaf.asset_invesment_planner.service.AssetService;
import com.copperleaf.asset_invesment_planner.util.mapper.AssetMapper;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
@Transactional
public class AssetServiceImpl implements AssetService {

    private final AssetRepository assetRepository;
    private final ProjectRepository projectRepository;
    private final AssetMapper assetMapper;

    @Autowired
    public AssetServiceImpl(AssetRepository assetRepository, ProjectRepository projectRepository, AssetMapper assetMapper) {
        this.assetRepository = assetRepository;
        this.projectRepository = projectRepository;
        this.assetMapper = assetMapper;
    }

    @Override
    public AssetResponse create(AssetCreateRequest dto) {
        Project project = this.projectRepository.findByCode(dto.getProjectCode())
                .orElseThrow(()-> new ProjectNotFoundException("Project not found: " +  dto.getProjectCode()));
        Asset saved = this.assetRepository.save(this.assetMapper.toEntity(dto, project));
        return assetMapper.toResponse(saved);
    }

    @Override
    public AssetResponse update(Long id, AssetUpdateRequest dto) {
        Asset asset = assetRepository.findById(id)
                .orElseThrow(() -> new AssetNotFoundException("Asset not found: " + id));

        assetMapper.mapUpdates(dto, asset);
        return assetMapper.toResponse(asset);
    }

    @Override
    public AssetResponse get(Long id) {
        return assetMapper.toResponse(this.assetRepository.findById(id).orElseThrow(()-> new AssetNotFoundException("Asset not found: " +  id)));
    }

    @Override
    public void delete(Long id) {
        Asset asset =  this.assetRepository.findById(id).orElseThrow(()-> new AssetNotFoundException("Asset not found: " +  id));
        this.assetRepository.delete(asset);
    }

    @Override
    public PageResponse<AssetResponse> listByProject(String projectCode, Pageable pageable) {
        Page<Asset> page = this.assetRepository.findByProject_Code(projectCode, pageable);
        return new PageResponse<>(
                page.map(this.assetMapper::toResponse).getContent(),
                page.getNumber(), page.getSize(), page.getTotalElements(), page.getTotalPages()
        );
    }

    @Override
    public ProjectAssetsSummaryResponse summarize(String projectCode) {

        Project project = this.projectRepository.findByCode(projectCode)
                .orElseThrow(()-> new ProjectNotFoundException("Project not found: " +  projectCode));

        var assets = assetRepository.findByProject_Code(projectCode, Pageable.unpaged()).getContent();

        BigDecimal totalReplacement = assets.stream()
                .map(Asset::getReplacementCost)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalCurrent = assets.stream()
                .map(Asset::getCurrentValue).reduce(BigDecimal.ZERO, BigDecimal::add);

        double util = project.getApprovedBudget().compareTo(BigDecimal.ZERO) == 0
                ? 0.0 : totalReplacement.divide(project.getApprovedBudget(), 4, RoundingMode.HALF_UP).doubleValue()*100.0;

        ProjectAssetsSummaryResponse response = new ProjectAssetsSummaryResponse();
        response.setProjectCode(project.getCode());
        response.setAssetCount(assets.size());
        response.setTotalReplacementCost(totalReplacement);
        response.setTotalCurrentValue(totalCurrent);
        response.setBudgetUtilizationPct(util);

        return response;
    }
}
