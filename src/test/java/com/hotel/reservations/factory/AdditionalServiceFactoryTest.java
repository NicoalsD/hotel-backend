package com.hotel.reservations.factory;

import com.hotel.reservations.model.AdditionalService;
import com.hotel.reservations.model.AdditionalServiceType;
import com.hotel.reservations.model.service.BreakfastService;
import com.hotel.reservations.model.service.SpaService;
import com.hotel.reservations.model.service.TransferService;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

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

    @Test
    void numbersServicesSequentiallyFromSrvOne() {
        AdditionalService first = factory.create(AdditionalServiceType.SPA, 1);
        AdditionalService second = factory.create(AdditionalServiceType.TRANSFER, 1);
        AdditionalService third = factory.create(AdditionalServiceType.BREAKFAST, 1);

        assertThat(first.getId()).isEqualTo("SRV-1");
        assertThat(second.getId()).isEqualTo("SRV-2");
        assertThat(third.getId()).isEqualTo("SRV-3");
    }

    @Test
    void eachFactoryInstanceKeepsItsOwnSequence() {
        factory.create(AdditionalServiceType.SPA, 1);

        assertThat(new AdditionalServiceFactory().create(AdditionalServiceType.SPA, 1).getId())
                .isEqualTo("SRV-1");
    }

    @Test
    void rejectsNullType() {
        assertThatThrownBy(() -> factory.create(null, 1)).isInstanceOf(NullPointerException.class);
    }
}
