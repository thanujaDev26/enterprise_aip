package com.copperleaf.asset_invesment_planner.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "asset_readings", indexes = {
        @Index(name = "ix_reading_asset_date", columnList = "asset_id, reading_date", unique = true)
})
public class AssetReading implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "asset_id", nullable = false)
    private Asset asset;

    @Column(name = "reading_date", nullable = false)
    private LocalDate readingDate;

    @Column(name="current_value", precision = 18, scale = 2, nullable = false)
    private BigDecimal currentValue;


}
