package com.hotel.reservations.pricing;

import com.hotel.reservations.model.Season;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.Month;
import java.util.EnumSet;
import java.util.Objects;
import java.util.Set;

/**
 * Calendario que determina la temporada de cada fecha. La temporada alta comprende
 * los meses de junio, julio, diciembre y enero; el resto del año es temporada baja.
 */
@Component
public class SeasonCalendar {

    private static final Set<Month> HIGH_MONTHS =
            EnumSet.of(Month.JUNE, Month.JULY, Month.DECEMBER, Month.JANUARY);

    /**
     * @param date fecha de la noche
     * @return {@link Season#HIGH} si el mes es de temporada alta, {@link Season#LOW} en otro caso
     */
    public Season resolveSeason(LocalDate date) {
        return isHighSeason(date) ? Season.HIGH : Season.LOW;
    }

    /**
     * @param date fecha a consultar; no debe ser nula
     * @return {@code true} si la fecha cae en junio, julio, diciembre o enero
     */
    public boolean isHighSeason(LocalDate date) {
        Objects.requireNonNull(date, "La fecha es obligatoria");
        return HIGH_MONTHS.contains(date.getMonth());
    }
}
