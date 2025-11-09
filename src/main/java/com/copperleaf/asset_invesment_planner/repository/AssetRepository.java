package com.copperleaf.asset_invesment_planner.repository;

import com.copperleaf.asset_invesment_planner.entity.Asset;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AssetRepository extends JpaRepository<Asset, Long> {

    Page<Asset> findByProject_Code(String projectCode, Pageable pageable);
}
