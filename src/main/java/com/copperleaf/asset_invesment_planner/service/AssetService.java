package com.copperleaf.asset_invesment_planner.service;


import com.copperleaf.asset_invesment_planner.dto.*;
import org.springframework.data.domain.Pageable;

public interface AssetService {

    AssetResponse create(AssetCreateRequest dto);

    AssetResponse update(Long id, AssetUpdateRequest dto);

    AssetResponse get(Long id);

    void delete(Long id);

    PageResponse<AssetResponse> listByProject(String projectCode, Pageable pageable);

    ProjectAssetsSummaryResponse summarize(String projectCode);
}
