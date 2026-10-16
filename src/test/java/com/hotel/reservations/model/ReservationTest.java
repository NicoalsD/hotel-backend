package com.hotel.reservations.model;

import com.hotel.reservations.model.service.SpaService;
import java.util.List;

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

    @Test
    void restoreRebuildsPersistedStateWithoutRevalidatingTransitions() {
        Room room = new Room(1L, "101", RoomType.SINGLE, 1, "Sencilla");
        Guest guest = new Guest("Ana Perez", "123", "ana@example.com", "300");
        DateRange range = new DateRange(LocalDate.of(2026, 3, 1), LocalDate.of(2026, 3, 3));
        LocalDateTime created = LocalDateTime.of(2026, 2, 1, 9, 0);
        LocalDateTime checkedIn = LocalDateTime.of(2026, 3, 1, 14, 0);
        DigitalKey key = DigitalKey.restore("code-1", "101", "RES-1001", checkedIn, true);
        AdditionalService spa = new SpaService("SRV-1", 2);

        Reservation reservation = Reservation.restore("RES-1001", guest, room, range, new BigDecimal("160.00"),
                created, ReservationStatus.CHECKED_IN, List.of(spa), key, null, checkedIn, null);

        assertThat(reservation.getId()).isEqualTo("RES-1001");
        assertThat(reservation.getCreatedAt()).isEqualTo(created);
        assertThat(reservation.getStatus()).isEqualTo(ReservationStatus.CHECKED_IN);
        assertThat(reservation.getDigitalKey()).isSameAs(key);
        assertThat(reservation.getCheckedInAt()).isEqualTo(checkedIn);
        assertThat(reservation.getCheckedOutAt()).isNull();
        assertThat(reservation.getServices()).containsExactly(spa);
        assertThat(reservation.getInvoiceNumber()).isNull();
    }

    @Test
    void restoredCheckedOutReservationKeepsInvoiceAndRejectsSecondCheckOut() {
        Room room = new Room(1L, "101", RoomType.SINGLE, 1, "Sencilla");
        DateRange range = new DateRange(LocalDate.of(2026, 3, 1), LocalDate.of(2026, 3, 3));
        Reservation reservation = Reservation.restore("RES-1002", new Guest("A", "1", "a@x.com", "1"), room,
                range, new BigDecimal("160.00"), LocalDateTime.now(), ReservationStatus.CHECKED_OUT,
                List.of(), null, "FAC-0004", LocalDateTime.now(), LocalDateTime.now());

        assertThat(reservation.getInvoiceNumber()).isEqualTo("FAC-0004");
        assertThatThrownBy(() -> reservation.checkOut("FAC-0005"))
                .isInstanceOf(InvalidReservationStateException.class);
    }
}
