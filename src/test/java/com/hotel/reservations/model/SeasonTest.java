package com.hotel.reservations.model;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class SeasonTest {

    @Test
    void highSeasonHasLabelAndMultiplierOneAndHalf() {
        assertThat(Season.HIGH.getLabel()).isEqualTo("Temporada alta");
        assertThat(Season.HIGH.getMultiplier()).isEqualByComparingTo(new BigDecimal("1.5"));
    }

    @Test
    void lowSeasonHasLabelAndNeutralMultiplier() {
        assertThat(Season.LOW.getLabel()).isEqualTo("Temporada baja");
        assertThat(Season.LOW.getMultiplier()).isEqualByComparingTo(BigDecimal.ONE);
    }
}
