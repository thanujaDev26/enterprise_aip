package com.copperleaf.asset_invesment_planner.service;


import com.copperleaf.asset_invesment_planner.entity.Asset;

import java.math.BigDecimal;

public interface ForecastService {

    BigDecimal forecastNextCurrentValue(Asset asset, double alpha);
}
