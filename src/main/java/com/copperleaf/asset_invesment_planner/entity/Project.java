package com.copperleaf.asset_invesment_planner.entity;


import com.copperleaf.asset_invesment_planner.entity.enums.ProjectStatus;
import com.copperleaf.asset_invesment_planner.entity.enums.Currency;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Project {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(nullable = false, unique = true, updatable = false,length = 64)
    private String code;

    @NotBlank
    @Column(nullable = false, length = 200)
    private String name;

    @Column(length = 200)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ProjectStatus status = ProjectStatus.DRAFT;

    @Column(precision = 18, scale = 2,nullable = false)
    private BigDecimal approvedBudget = BigDecimal.ZERO;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 8)
    private Currency currency = Currency.USD;

    private LocalDate startDate;
    private LocalDate endDate;

    @Column(nullable = false)
    private Integer riskScore = 0;

    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Asset>  assets = new ArrayList<>();

    public void addAsset(Asset asset){
        assets.add(asset);
        asset.setProject(this);
    }

    public void removeAsset(Asset asset){
        assets.remove(asset);
        asset.setProject(null);
    }

}
