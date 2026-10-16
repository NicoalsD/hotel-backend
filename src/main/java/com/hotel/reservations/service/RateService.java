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

@Service
public class RateService {

    private final SeasonCalendar seasonCalendar;
    private final PricingStrategyResolver strategyResolver;

    public RateService(SeasonCalendar seasonCalendar, PricingStrategyResolver strategyResolver) {
        this.seasonCalendar = seasonCalendar;
        this.strategyResolver = strategyResolver;
    }

    public PriceQuote calculateQuote(RoomType type, DateRange range) {
        List<NightlyRate> rates = new ArrayList<>();
        for (LocalDate date : range.getNightDates()) {
            Season season = resolveSeason(date);
            BigDecimal price = strategyResolver.resolve(season).calculateNightlyPrice(type.getBasePrice());
            rates.add(new NightlyRate(date, season, price));
        }
        return new PriceQuote(type, range, rates);
    }

    public BigDecimal calculateTotal(RoomType type, DateRange range) {
        return calculateQuote(type, range).getTotal();
    }

    public Season resolveSeason(LocalDate date) {
        return seasonCalendar.resolveSeason(date);
    }
}
