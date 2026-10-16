package com.hotel.reservations.service;

import com.hotel.reservations.model.DateRange;
import com.hotel.reservations.model.DigitalKey;
import com.hotel.reservations.model.Guest;
import com.hotel.reservations.model.Reservation;
import com.hotel.reservations.model.Room;
import com.hotel.reservations.model.RoomType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AccessServiceTest {

    private AccessService accessService;
    private Reservation reservation;

    @BeforeEach
    void setUp() {
        accessService = new AccessService();
        Guest guest = new Guest("Ana Pérez", "1020304050", "ana@correo.com", "3001234567");
        Room room = new Room(7L, "201", RoomType.DOUBLE, 2, "Habitación doble");
        DateRange range = new DateRange(LocalDate.of(2026, 11, 1), LocalDate.of(2026, 11, 4));
        reservation = new Reservation("RES-1001", guest, room, range, new BigDecimal("360.00"));
    }

    @Test
    void generatesActiveUuidKeyForReservationRoom() {
        DigitalKey key = accessService.generateKey(reservation);

        assertThat(UUID.fromString(key.getCode())).isNotNull();
        assertThat(key.getRoomNumber()).isEqualTo("201");
        assertThat(key.getReservationId()).isEqualTo("RES-1001");
        assertThat(key.isActive()).isTrue();
    }

    @Test
    void generatesDifferentCodesForEachKey() {
        String first = accessService.generateKey(reservation).getCode();
        String second = accessService.generateKey(reservation).getCode();

        assertThat(first).isNotEqualTo(second);
    }

    @Test
    void validatesAccessOnlyForIssuedKeyAndItsRoom() {
        DigitalKey key = accessService.generateKey(reservation);

        assertThat(accessService.validateAccess(key.getCode(), "201")).isTrue();
        assertThat(accessService.validateAccess(key.getCode(), "202")).isFalse();
        assertThat(accessService.validateAccess("no-existe", "201")).isFalse();
    }

    @Test
    void revokedKeyNoLongerOpensRoom() {
        DigitalKey key = accessService.generateKey(reservation);
        reservation.checkIn(key);

        accessService.revokeKey(reservation);

        assertThat(key.isActive()).isFalse();
        assertThat(accessService.validateAccess(key.getCode(), "201")).isFalse();
    }

    @Test
    void revokeWithoutKeyDoesNothing() {
        accessService.revokeKey(reservation);

        assertThat(reservation.getDigitalKey()).isNull();
    }

    @Test
    void nullKeyCodeIsNotValid() {
        assertThat(accessService.validateAccess(null, "201")).isFalse();
    }

    @Test
    void rejectsNullReservation() {
        assertThatThrownBy(() -> accessService.generateKey(null)).isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> accessService.revokeKey(null)).isInstanceOf(NullPointerException.class);
    }
}
