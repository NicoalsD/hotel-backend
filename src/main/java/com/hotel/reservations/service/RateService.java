package com.hotel.reservations.service;

import com.hotel.reservations.model.DateRange;
import com.hotel.reservations.model.NightlyRate;
import com.hotel.reservations.model.PriceQuote;
import com.hotel.reservations.model.RoomType;
import com.hotel.reservations.model.Season;
import com.hotel.reservations.pricing.PricingStrategyResolver;
import com.hotel.reservations.pricing.SeasonCalendar;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Servicio de tarifas (TarifaService). Calcula el precio de cada noche según la temporada
 * de la fecha y la tarifa base del tipo de habitación.
 */
@Service
public class RateService {

    private final SeasonCalendar seasonCalendar;
    private final PricingStrategyResolver strategyResolver;

    /**
     * @param seasonCalendar calendario que determina la temporada de cada fecha
     * @param strategyResolver resuelve la estrategia de precio para cada temporada
     */
    public RateService(SeasonCalendar seasonCalendar, PricingStrategyResolver strategyResolver) {
        this.seasonCalendar = seasonCalendar;
        this.strategyResolver = strategyResolver;
    }

    /**
     * Cotiza una estancia noche a noche. Cada noche (desde la entrada inclusive hasta la salida
     * exclusive) recibe la temporada de su fecha y su precio.
     *
     * @param type tipo de habitación
     * @param range rango de fechas de la estancia
     * @return cotización con el detalle de cada noche
     */
    public PriceQuote calculateQuote(RoomType type, DateRange range) {
        Objects.requireNonNull(type, "El tipo de habitación es obligatorio");
        Objects.requireNonNull(range, "El rango de fechas es obligatorio");
        List<NightlyRate> rates = new ArrayList<>();
        for (LocalDate date : range.getNightDates()) {
            Season season = resolveSeason(date);
            BigDecimal price = strategyResolver.resolve(season).calculateNightlyPrice(type.getBasePrice());
            rates.add(new NightlyRate(date, season, price));
        }
        return new PriceQuote(type, range, rates);
    }

    /**
     * @param type tipo de habitación
     * @param range rango de fechas de la estancia
     * @return total de la estancia con escala 2
     */
    public BigDecimal calculateTotal(RoomType type, DateRange range) {
        return calculateQuote(type, range).getTotal();
    }

    /**
     * @param date fecha de una noche
     * @return temporada que corresponde a la fecha
     */
    public Season resolveSeason(LocalDate date) {
        return seasonCalendar.resolveSeason(date);
    }
}
