package com.copperleaf.asset_invesment_planner.service.impl;

import com.copperleaf.asset_invesment_planner.entity.Asset;
import com.copperleaf.asset_invesment_planner.entity.AssetReading;
import com.copperleaf.asset_invesment_planner.repository.AssetReadingRepository;
import com.copperleaf.asset_invesment_planner.service.ForecastService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
@Transactional
public class ForecastServiceImpl implements ForecastService {

    private final AssetReadingRepository assetReadingRepository;

    @Autowired
    public ForecastServiceImpl(AssetReadingRepository assetReadingRepository) {
        this.assetReadingRepository = assetReadingRepository;
    }

    @Override
    public BigDecimal forecastNextCurrentValue(Asset asset, double alpha) {
        List<AssetReading> series = assetReadingRepository.findByAssetOrderByReadingDateAsc(asset);

        if (series.isEmpty()) {
            return null;
        }

        BigDecimal F = series.get(0).getCurrentValue();

        for(int i=1;i< series.size();i++){
            BigDecimal Y = series.get(i).getCurrentValue();
            // F_t = alpha*Y_{t-1} + (1-alpha)*F_{t-1}
            F = Y.multiply(bd(alpha)).add(F.multiply(bd(1 - alpha)));
        }

        return F.setScale(2, RoundingMode.HALF_UP);
    }

    private BigDecimal bd(double d){
        return new BigDecimal(Double.toString(d));
    }
}
