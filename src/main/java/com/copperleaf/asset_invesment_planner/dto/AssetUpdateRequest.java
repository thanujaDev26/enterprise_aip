package com.copperleaf.asset_invesment_planner.dto;

import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Data
public class AssetUpdateRequest {

    @NotBlank
    @Size(max = 160)
    private String name;

    @NotBlank
    @Size(max = 64)
    private String type;

    @NotNull
    @DecimalMin("0.00")
    private BigDecimal replacementCost;

    @NotNull
    @DecimalMin("0.00")
    private BigDecimal currentValue;

    @Min(0)
    @Max(100)
    private Integer healthIndex;

    @NotNull
    private Double initialInvestment;

    @NotNull
    private Double roi;
}
