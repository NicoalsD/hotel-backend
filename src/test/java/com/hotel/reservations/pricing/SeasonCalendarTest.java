package com.hotel.reservations.pricing;

import com.hotel.reservations.model.Season;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

class SeasonCalendarTest {

    private final SeasonCalendar calendar = new SeasonCalendar();

    @Test
    void juneStartsHighSeasonAndMayEndsLowSeason() {
        assertThat(calendar.resolveSeason(LocalDate.of(2026, 5, 31))).isEqualTo(Season.LOW);
        assertThat(calendar.resolveSeason(LocalDate.of(2026, 6, 1))).isEqualTo(Season.HIGH);
    }

    @Test
    void julyEndsHighSeasonAndAugustIsLow() {
        assertThat(calendar.resolveSeason(LocalDate.of(2026, 7, 31))).isEqualTo(Season.HIGH);
        assertThat(calendar.resolveSeason(LocalDate.of(2026, 8, 1))).isEqualTo(Season.LOW);
    }

    @Test
    void novemberIsLowAndDecemberStartsHighSeason() {
        assertThat(calendar.resolveSeason(LocalDate.of(2026, 11, 30))).isEqualTo(Season.LOW);
        assertThat(calendar.resolveSeason(LocalDate.of(2026, 12, 1))).isEqualTo(Season.HIGH);
    }

    @Test
    void januaryEndsHighSeasonAndFebruaryIsLow() {
        assertThat(calendar.resolveSeason(LocalDate.of(2027, 1, 31))).isEqualTo(Season.HIGH);
        assertThat(calendar.resolveSeason(LocalDate.of(2027, 2, 1))).isEqualTo(Season.LOW);
    }

    @Test
    void isHighSeasonMatchesResolveSeason() {
        LocalDate date = LocalDate.of(2026, 6, 15);

        assertThat(calendar.isHighSeason(date)).isTrue();
        assertThat(calendar.isHighSeason(LocalDate.of(2026, 3, 15))).isFalse();
    }
}
