package com.hotel.reservations.pricing;

import com.hotel.reservations.model.Season;
import org.springframework.stereotype.Component;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@Component
public class PricingStrategyResolver {

    private final Map<Season, PricingStrategy> strategies = new EnumMap<>(Season.class);

    public PricingStrategyResolver(List<PricingStrategy> strategies) {
        for (PricingStrategy strategy : strategies) {
            this.strategies.put(strategy.getSeason(), strategy);
        }
    }

    public PricingStrategy resolve(Season season) {
        PricingStrategy strategy = strategies.get(season);
        if (strategy == null) {
            throw new IllegalStateException("No existe estrategia de tarifa para la temporada " + season);
        }
        return strategy;
    }
}
