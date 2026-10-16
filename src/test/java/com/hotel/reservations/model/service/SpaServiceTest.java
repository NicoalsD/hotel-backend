package com.hotel.reservations.model.service;

import com.hotel.reservations.model.AdditionalServiceType;
import com.hotel.reservations.model.ChargeMode;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class SpaServiceTest {

    @Test
    void chargesOnceRegardlessOfNights() {
        SpaService spa = new SpaService("SRV-1", 5);

        assertThat(spa.getQuantity()).isEqualTo(1);
        assertThat(spa.getTotal()).isEqualTo(new BigDecimal("50.00"));
    }

    @Test
    void exposesCatalogDataForSpa() {
        SpaService spa = new SpaService("SRV-1", 2);

        assertThat(spa.getId()).isEqualTo("SRV-1");
        assertThat(spa.getType()).isEqualTo(AdditionalServiceType.SPA);
        assertThat(spa.getType().getChargeMode()).isEqualTo(ChargeMode.FLAT);
        assertThat(spa.getLabel()).isEqualTo("Spa");
        assertThat(spa.getUnitPrice()).isEqualTo(new BigDecimal("50.00"));
        assertThat(spa.getAddedAt()).isNotNull();
    }
}
