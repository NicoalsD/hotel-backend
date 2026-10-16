package com.hotel.reservations.service;

import com.hotel.reservations.factory.AdditionalServiceFactory;
import com.hotel.reservations.model.AdditionalServiceType;
import com.hotel.reservations.model.DateRange;
import com.hotel.reservations.model.Guest;
import com.hotel.reservations.model.Reservation;
import com.hotel.reservations.model.Room;
import com.hotel.reservations.model.RoomType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

class AdditionalServiceServiceTest {

    private AdditionalServiceService service;
    private Reservation reservation;

    @BeforeEach
    void setUp() {
        service = new AdditionalServiceService(new AdditionalServiceFactory());
        Guest guest = new Guest("Ana Pérez", "1020304050", "ana@correo.com", "3001234567");
        Room room = new Room(7L, "201", RoomType.DOUBLE, 2, "Habitación doble");
        DateRange range = new DateRange(LocalDate.of(2026, 11, 1), LocalDate.of(2026, 11, 4));
        reservation = new Reservation("RES-1001", guest, room, range, new BigDecimal("360.00"));
    }

    @Test
    void addsServiceWithReservationNightsAndSequentialId() {
        var added = service.addService(reservation, AdditionalServiceType.BREAKFAST);

        assertThat(added.getId()).isEqualTo("SRV-1");
        assertThat(added.getQuantity()).isEqualTo(3);
        assertThat(reservation.getServices()).containsExactly(added);
    }

    @Test
    void calculatesServicesTotalFromAllServices() {
        service.addService(reservation, AdditionalServiceType.BREAKFAST);
        service.addService(reservation, AdditionalServiceType.SPA);
        service.addService(reservation, AdditionalServiceType.TRANSFER);

        assertThat(service.calculateServicesTotal(reservation)).isEqualTo(new BigDecimal("125.00"));
    }

    @Test
    void servicesTotalIsZeroWithScaleTwoWhenNoServices() {
        assertThat(service.calculateServicesTotal(reservation)).isEqualTo(new BigDecimal("0.00"));
    }

    @Test
    void catalogListsEveryServiceTypeInOrder() {
        assertThat(service.getCatalog()).containsExactly(
                AdditionalServiceType.SPA, AdditionalServiceType.BREAKFAST, AdditionalServiceType.TRANSFER);
    }
}
