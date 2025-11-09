package com.copperleaf.asset_invesment_planner.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "assets", indexes = {
        @Index(name = "ix_asset_project", columnList = "project_id")
})
public class Asset implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 160)
    private String name;

    @Column(nullable = false, length = 64)
    private String type;

    @Column(precision = 18, scale = 2, nullable = false)
    private BigDecimal replacementCost = BigDecimal.ZERO;

    @Column(nullable = false)
    private Integer healthIndex = 100;

    @Column(nullable = false)
    private Double initialInvestment;

    @Column(precision = 18, scale = 2, nullable = false)
    private BigDecimal currentValue = BigDecimal.ZERO;

    @Column(nullable = false)
    private Double roi;

    @ManyToOne(fetch = FetchType.LAZY,optional = false)
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;
}
