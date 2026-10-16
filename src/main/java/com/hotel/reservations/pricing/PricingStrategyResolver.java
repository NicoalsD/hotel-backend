package com.hotel.reservations.pricing;

import com.hotel.reservations.model.Season;
import org.springframework.stereotype.Component;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Resuelve la estrategia de tarificación correspondiente a una temporada.
 * Las estrategias se inyectan por Spring y se indexan por temporada.
 */
@Component
public class PricingStrategyResolver {

    private final Map<Season, PricingStrategy> strategies = new EnumMap<>(Season.class);

    /**
     * @param strategies todas las estrategias disponibles; cada temporada debe tener una
     */
    public PricingStrategyResolver(List<PricingStrategy> strategies) {
        for (PricingStrategy strategy : strategies) {
            this.strategies.put(strategy.getSeason(), strategy);
        }
    }

    /**
     * @param season temporada a resolver; no debe ser nula
     * @return estrategia de la temporada indicada
     * @throws IllegalStateException si no hay estrategia registrada para esa temporada
     */
    public PricingStrategy resolve(Season season) {
        Objects.requireNonNull(season, "La temporada es obligatoria");
        PricingStrategy strategy = strategies.get(season);
        if (strategy == null) {
            throw new IllegalStateException("No existe estrategia de tarifa para la temporada " + season);
        }
        return strategy;
    }
}
