package com.hotel.reservations.model;

import com.hotel.reservations.exception.InvalidReservationStateException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ReservationTest {

    private Reservation reservation;
    private DateRange range;

    /** Servicio de prueba con cantidad fija, para no depender de las subclases de producción. */
    private static class FixedQuantityService extends AdditionalService {

        FixedQuantityService(String id, AdditionalServiceType type, int quantity) {
            super(id, type, quantity);
        }

        @Override
        protected int calculateQuantity(long reservationNights) {
            return (int) reservationNights;
        }
    }

    @BeforeEach
    void setUp() {
        range = new DateRange(LocalDate.of(2026, 11, 1), LocalDate.of(2026, 11, 4));
        Room room = new Room(7L, "201", RoomType.DOUBLE, 2, "Habitación doble");
        Guest guest = new Guest("Ana Pérez", "1020304050", "ana@correo.com", "3001234567");
        reservation = new Reservation("RES-1001", guest, room, range, new BigDecimal("360.00"));
    }

    @Test
    void newReservationStartsConfirmedWithoutKeyOrInvoice() {
        assertThat(reservation.getStatus()).isEqualTo(ReservationStatus.CONFIRMED);
        assertThat(reservation.getDigitalKey()).isNull();
        assertThat(reservation.getInvoiceNumber()).isNull();
        assertThat(reservation.getCheckedInAt()).isNull();
        assertThat(reservation.getCheckedOutAt()).isNull();
        assertThat(reservation.getCreatedAt()).isNotNull();
        assertThat(reservation.getNights()).isEqualTo(3);
        assertThat(reservation.isActive()).isTrue();
        assertThat(reservation.getGuest().getFullName()).isEqualTo("Ana Pérez");
    }

    @Test
    void checkInMovesToCheckedInAndStoresKey() {
        DigitalKey key = new DigitalKey("code-1", "201", "RES-1001", LocalDateTime.now());

        reservation.checkIn(key);

        assertThat(reservation.getStatus()).isEqualTo(ReservationStatus.CHECKED_IN);
        assertThat(reservation.getDigitalKey()).isSameAs(key);
        assertThat(reservation.getCheckedInAt()).isNotNull();
        assertThat(reservation.isActive()).isTrue();
    }

    @Test
    void checkInTwiceIsRejected() {
        DigitalKey key = new DigitalKey("code-1", "201", "RES-1001", LocalDateTime.now());
        reservation.checkIn(key);

        assertThatThrownBy(() -> reservation.checkIn(key))
                .isInstanceOf(InvalidReservationStateException.class)
                .hasMessage("La reserva RES-1001 no puede hacer check-in porque su estado es Check-in realizado");
    }

    @Test
    void checkOutRequiresCheckedIn() {
        assertThatThrownBy(() -> reservation.checkOut("FAC-0001"))
                .isInstanceOf(InvalidReservationStateException.class)
                .hasMessage("La reserva RES-1001 no puede hacer check-out porque su estado es Confirmada");
    }

    @Test
    void checkOutFinishesReservation() {
        reservation.checkIn(new DigitalKey("code-1", "201", "RES-1001", LocalDateTime.now()));

        reservation.checkOut("FAC-0001");

        assertThat(reservation.getStatus()).isEqualTo(ReservationStatus.CHECKED_OUT);
        assertThat(reservation.getInvoiceNumber()).isEqualTo("FAC-0001");
        assertThat(reservation.getCheckedOutAt()).isNotNull();
        assertThat(reservation.isActive()).isFalse();
        assertThat(reservation.canAddServices()).isFalse();
    }

    @Test
    void servicesCanBeAddedWhileConfirmedOrCheckedIn() {
        reservation.addService(new FixedQuantityService("SRV-1", AdditionalServiceType.SPA, 1));
        reservation.checkIn(new DigitalKey("code-1", "201", "RES-1001", LocalDateTime.now()));
        reservation.addService(new FixedQuantityService("SRV-2", AdditionalServiceType.TRANSFER, 1));

        assertThat(reservation.getServices()).hasSize(2);
    }

    @Test
    void servicesCannotBeAddedAfterCheckOut() {
        reservation.checkIn(new DigitalKey("code-1", "201", "RES-1001", LocalDateTime.now()));
        reservation.checkOut("FAC-0001");

        assertThatThrownBy(() -> reservation.addService(
                new FixedQuantityService("SRV-1", AdditionalServiceType.SPA, 1)))
                .isInstanceOf(InvalidReservationStateException.class)
                .hasMessage("La reserva RES-1001 no permite agregar servicios porque su estado es Finalizada");
    }

    @Test
    void servicesTotalAndEstimatedTotalIncludeServices() {
        reservation.addService(new FixedQuantityService("SRV-1", AdditionalServiceType.SPA, 1));
        reservation.addService(new FixedQuantityService("SRV-2", AdditionalServiceType.BREAKFAST, 3));

        assertThat(reservation.getServicesTotal()).isEqualTo(new BigDecimal("95.00"));
        assertThat(reservation.getEstimatedTotal()).isEqualTo(new BigDecimal("455.00"));
        assertThat(reservation.getRoomTotal()).isEqualTo(new BigDecimal("360.00"));
    }

    @Test
    void servicesTotalIsZeroWithScaleTwoWhenEmpty() {
        assertThat(reservation.getServicesTotal()).isEqualTo(new BigDecimal("0.00"));
        assertThat(reservation.getEstimatedTotal()).isEqualTo(new BigDecimal("360.00"));
    }

    @Test
    void servicesViewCannotBeModified() {
        reservation.addService(new FixedQuantityService("SRV-1", AdditionalServiceType.SPA, 1));

        assertThatThrownBy(() -> reservation.getServices().clear())
                .isInstanceOf(UnsupportedOperationException.class);
    }
}
