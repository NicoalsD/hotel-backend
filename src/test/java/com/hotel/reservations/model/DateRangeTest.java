package com.hotel.reservations.model;

import com.hotel.reservations.exception.InvalidDateRangeException;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class DateRangeTest {

    @Test
    void countsNightsAsDifferenceBetweenDates() {
        DateRange range = new DateRange(LocalDate.of(2026, 11, 1), LocalDate.of(2026, 11, 4));

        assertThat(range.getNights()).isEqualTo(3);
    }

    @Test
    void listsNightDatesExcludingCheckOut() {
        DateRange range = new DateRange(LocalDate.of(2026, 12, 30), LocalDate.of(2027, 1, 2));

        assertThat(range.getNightDates()).containsExactly(
                LocalDate.of(2026, 12, 30),
                LocalDate.of(2026, 12, 31),
                LocalDate.of(2027, 1, 1));
    }

    @Test
    void rejectsNullDates() {
        assertThatThrownBy(() -> new DateRange(null, LocalDate.of(2026, 11, 4)))
                .isInstanceOf(InvalidDateRangeException.class)
                .hasMessage("Las fechas de entrada y salida son obligatorias");
        assertThatThrownBy(() -> new DateRange(LocalDate.of(2026, 11, 1), null))
                .isInstanceOf(InvalidDateRangeException.class);
    }

    @Test
    void rejectsCheckOutNotAfterCheckIn() {
        LocalDate day = LocalDate.of(2026, 11, 1);

        assertThatThrownBy(() -> new DateRange(day, day))
                .isInstanceOf(InvalidDateRangeException.class)
                .hasMessage("La fecha de salida debe ser posterior a la fecha de entrada");
        assertThatThrownBy(() -> new DateRange(day, day.minusDays(1)))
                .isInstanceOf(InvalidDateRangeException.class);
    }

    @Test
    void adjacentRangesDoNotOverlap() {
        DateRange first = new DateRange(LocalDate.of(2026, 11, 1), LocalDate.of(2026, 11, 4));
        DateRange second = new DateRange(LocalDate.of(2026, 11, 4), LocalDate.of(2026, 11, 6));

        assertThat(first.overlaps(second)).isFalse();
        assertThat(second.overlaps(first)).isFalse();
    }

    @Test
    void partialAndContainedRangesOverlap() {
        DateRange base = new DateRange(LocalDate.of(2026, 11, 1), LocalDate.of(2026, 11, 5));

        assertThat(base.overlaps(new DateRange(LocalDate.of(2026, 11, 4), LocalDate.of(2026, 11, 8)))).isTrue();
        assertThat(base.overlaps(new DateRange(LocalDate.of(2026, 10, 28), LocalDate.of(2026, 11, 2)))).isTrue();
        assertThat(base.overlaps(new DateRange(LocalDate.of(2026, 11, 2), LocalDate.of(2026, 11, 3)))).isTrue();
    }

    @Test
    void equalRangesAreEqualAndShareHashCode() {
        DateRange a = new DateRange(LocalDate.of(2026, 11, 1), LocalDate.of(2026, 11, 4));
        DateRange b = new DateRange(LocalDate.of(2026, 11, 1), LocalDate.of(2026, 11, 4));
        DateRange c = new DateRange(LocalDate.of(2026, 11, 1), LocalDate.of(2026, 11, 5));

        assertThat(a).isEqualTo(b).hasSameHashCodeAs(b);
        assertThat(a).isNotEqualTo(c);
        assertThat(a.toString()).isEqualTo("2026-11-01/2026-11-04");
        assertThat(List.of(a)).contains(b);
    }
}
