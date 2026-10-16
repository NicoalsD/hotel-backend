package com.hotel.reservations.model.service;

import com.hotel.reservations.model.AdditionalServiceType;
import com.hotel.reservations.model.ChargeMode;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class TransferServiceTest {

    @Test
    void chargesOnceRegardlessOfNights() {
        TransferService transfer = new TransferService("SRV-4", 7);

        assertThat(transfer.getQuantity()).isEqualTo(1);
        assertThat(transfer.getTotal()).isEqualTo(new BigDecimal("30.00"));
    }

    @Test
    void exposesFlatCatalogData() {
        TransferService transfer = new TransferService("SRV-4", 1);

        assertThat(transfer.getType()).isEqualTo(AdditionalServiceType.TRANSFER);
        assertThat(transfer.getType().getChargeMode()).isEqualTo(ChargeMode.FLAT);
        assertThat(transfer.getLabel()).isEqualTo("Traslado");
        assertThat(transfer.getAddedAt()).isNotNull();
    }
}
