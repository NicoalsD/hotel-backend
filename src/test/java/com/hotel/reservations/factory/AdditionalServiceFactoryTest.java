package com.hotel.reservations.factory;

import com.hotel.reservations.model.AdditionalServiceType;
import com.hotel.reservations.model.service.BreakfastService;
import com.hotel.reservations.model.service.SpaService;
import com.hotel.reservations.model.service.TransferService;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class AdditionalServiceFactoryTest {

    private final AdditionalServiceFactory factory = new AdditionalServiceFactory();

    @Test
    void createsSubclassMatchingEachType() {
        assertThat(factory.create(AdditionalServiceType.SPA, 2)).isInstanceOf(SpaService.class);
        assertThat(factory.create(AdditionalServiceType.BREAKFAST, 2)).isInstanceOf(BreakfastService.class);
        assertThat(factory.create(AdditionalServiceType.TRANSFER, 2)).isInstanceOf(TransferService.class);
    }

    @Test
    void passesReservationNightsToBreakfastQuantity() {
        assertThat(factory.create(AdditionalServiceType.BREAKFAST, 4).getQuantity()).isEqualTo(4);
        assertThat(factory.create(AdditionalServiceType.SPA, 4).getQuantity()).isEqualTo(1);
    }
}
