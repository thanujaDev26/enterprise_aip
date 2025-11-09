package com.copperleaf.asset_invesment_planner.repository;

import com.copperleaf.asset_invesment_planner.entity.Asset;
import com.copperleaf.asset_invesment_planner.entity.AssetReading;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
@EnableJpaRepositories
public interface AssetReadingRepository extends JpaRepository<AssetReading, Long> {
    List<AssetReading> findByAssetOrderByReadingDateAsc(Asset asset);
    List<AssetReading> findByAssetAndReadingDateBetweenOrderByReadingDateAsc(Asset asset, LocalDate from, LocalDate to);
}
