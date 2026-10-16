package com.hotel.reservations.pricing;

import com.hotel.reservations.model.Season;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.Month;
import java.util.EnumSet;
import java.util.Objects;
import java.util.Set;

@Component
public class SeasonCalendar {

    private static final Set<Month> HIGH_MONTHS =
            EnumSet.of(Month.JUNE, Month.JULY, Month.DECEMBER, Month.JANUARY);

    public Season resolveSeason(LocalDate date) {
        return isHighSeason(date) ? Season.HIGH : Season.LOW;
    }

    public boolean isHighSeason(LocalDate date) {
        Objects.requireNonNull(date, "La fecha es obligatoria");
        return HIGH_MONTHS.contains(date.getMonth());
    }
}
