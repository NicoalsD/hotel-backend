package com.hotel.reservations.service;

import com.hotel.reservations.exception.InvalidReservationStateException;
import com.hotel.reservations.factory.AdditionalServiceFactory;
import com.hotel.reservations.model.AdditionalServiceType;
import com.hotel.reservations.model.DateRange;
import com.hotel.reservations.model.DigitalKey;
import com.hotel.reservations.model.Guest;
import com.hotel.reservations.model.Reservation;
import com.hotel.reservations.model.ReservationStatus;
import com.hotel.reservations.model.Room;
import com.hotel.reservations.model.RoomType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

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
    void allowsServicesWhileConfirmedOrCheckedIn() {
        service.addService(reservation, AdditionalServiceType.SPA);
        reservation.checkIn(new DigitalKey("KEY-1", "201", "RES-1001", LocalDateTime.now()));
        service.addService(reservation, AdditionalServiceType.TRANSFER);

        assertThat(reservation.getServices()).hasSize(2);
        assertThat(reservation.getStatus()).isEqualTo(ReservationStatus.CHECKED_IN);
    }

    @Test
    void rejectsServicesAfterCheckOutAndKeepsServicesUnchanged() {
        service.addService(reservation, AdditionalServiceType.SPA);
        reservation.checkIn(new DigitalKey("KEY-1", "201", "RES-1001", LocalDateTime.now()));
        reservation.checkOut("FAC-0001");

        assertThatThrownBy(() -> service.addService(reservation, AdditionalServiceType.BREAKFAST))
                .isInstanceOf(InvalidReservationStateException.class)
                .hasMessageContaining("Finalizada");
        assertThat(reservation.getServices()).hasSize(1);
        assertThat(service.calculateServicesTotal(reservation)).isEqualTo(new BigDecimal("50.00"));
    }

    @Test
    void rejectsMissingReservationOrType() {
        assertThatThrownBy(() -> service.addService(null, AdditionalServiceType.SPA))
                .isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> service.addService(reservation, null))
                .isInstanceOf(NullPointerException.class);
    }
}
