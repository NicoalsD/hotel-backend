package com.hotel.reservations.model.service;

import com.hotel.reservations.model.AdditionalServiceType;
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
}
