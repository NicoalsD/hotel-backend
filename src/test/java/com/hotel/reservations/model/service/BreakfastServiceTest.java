package com.hotel.reservations.model.service;

import com.hotel.reservations.model.AdditionalServiceType;
import com.hotel.reservations.model.ChargeMode;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class BreakfastServiceTest {

    @Test
    void chargesOncePerNight() {
        BreakfastService breakfast = new BreakfastService("SRV-2", 3);

        assertThat(breakfast.getQuantity()).isEqualTo(3);
        assertThat(breakfast.getTotal()).isEqualTo(new BigDecimal("45.00"));
    }

    @Test
    void singleNightStayChargesOneBreakfast() {
        BreakfastService breakfast = new BreakfastService("SRV-3", 1);

        assertThat(breakfast.getQuantity()).isEqualTo(1);
        assertThat(breakfast.getTotal()).isEqualTo(new BigDecimal("15.00"));
    }

    @Test
    void exposesPerNightCatalogData() {
        BreakfastService breakfast = new BreakfastService("SRV-2", 2);

        assertThat(breakfast.getType()).isEqualTo(AdditionalServiceType.BREAKFAST);
        assertThat(breakfast.getType().getChargeMode()).isEqualTo(ChargeMode.PER_NIGHT);
        assertThat(breakfast.getLabel()).isEqualTo("Desayuno");
        assertThat(breakfast.getUnitPrice()).isEqualTo(new BigDecimal("15.00"));
    }
}
