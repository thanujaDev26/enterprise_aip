package com.copperleaf.asset_invesment_planner.repository;

import com.copperleaf.asset_invesment_planner.entity.Project;
import com.copperleaf.asset_invesment_planner.entity.enums.ProjectStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Optional;

@Repository
@EnableJpaRepositories
public interface ProjectRepository extends JpaRepository<Project, Long>, JpaSpecificationExecutor<Project> {

    Optional<Project> findByCode(String code);

    boolean existsByCode(String code);

    Page<Project> findByStatus(ProjectStatus status, Pageable pageable);

    @Query("SELECT coalesce (SUM(p.approvedBudget),0) FROM Project p WHERE p.status=:status")
    BigDecimal sumBudgetByStatus(@Param("status")  ProjectStatus status);

}
