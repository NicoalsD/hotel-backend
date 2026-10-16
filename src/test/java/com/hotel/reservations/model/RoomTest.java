package com.hotel.reservations.model;

import com.hotel.reservations.exception.RoomNotAvailableException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RoomTest {

    private Room room;

    @BeforeEach
    void setUp() {
        room = new Room(7L, "201", RoomType.DOUBLE, 2, "Habitación doble con vista a la ciudad");
    }

    @Test
    void newRoomIsAvailableAndDelegatesTypeData() {
        assertThat(room.getStatus()).isEqualTo(RoomStatus.AVAILABLE);
        assertThat(room.getBasePrice()).isEqualByComparingTo(new BigDecimal("120.00"));
        assertThat(room.getCapacity()).isEqualTo(2);
        assertThat(room.getNumber()).isEqualTo("201");
        assertThat(room.getFloor()).isEqualTo(2);
        assertThat(room.getBookings()).isEmpty();
    }

    @Test
    void roomWithoutBookingsIsAvailableForAnyRange() {
        assertThat(room.isAvailableFor(range(1, 4))).isTrue();
    }

    @Test
    void overlappingBookingMakesRoomUnavailable() {
        room.addBooking("RES-1001", range(1, 4));

        assertThat(room.isAvailableFor(range(3, 6))).isFalse();
        assertThat(room.isAvailableFor(range(1, 4))).isFalse();
        assertThat(room.getBookings()).containsKey("RES-1001");
    }

    @Test
    void adjacentBookingIsAllowed() {
        room.addBooking("RES-1001", range(1, 4));
        room.addBooking("RES-1002", range(4, 6));

        assertThat(room.getBookings()).hasSize(2);
    }

    @Test
    void addingOverlappingBookingThrowsConflict() {
        room.addBooking("RES-1001", range(1, 4));

        assertThatThrownBy(() -> room.addBooking("RES-1002", range(2, 3)))
                .isInstanceOf(RoomNotAvailableException.class)
                .hasMessage("La habitación 201 no está disponible en las fechas seleccionadas");
        assertThat(room.getBookings()).doesNotContainKey("RES-1002");
    }

    @Test
    void removingBookingFreesTheRange() {
        room.addBooking("RES-1001", range(1, 4));

        room.removeBooking("RES-1001");

        assertThat(room.isAvailableFor(range(1, 4))).isTrue();
    }

    @Test
    void removingUnknownBookingDoesNothing() {
        room.addBooking("RES-1001", range(1, 4));

        room.removeBooking("RES-9999");

        assertThat(room.getBookings()).containsKey("RES-1001");
    }

    @Test
    void occupiedAndAvailableStatusCanBeSwitched() {
        room.markOccupied();
        assertThat(room.getStatus()).isEqualTo(RoomStatus.OCCUPIED);

        room.markAvailable();
        assertThat(room.getStatus()).isEqualTo(RoomStatus.AVAILABLE);
    }

    @Test
    void bookingsViewCannotBeModified() {
        room.addBooking("RES-1001", range(1, 4));

        assertThatThrownBy(() -> room.getBookings().put("RES-X", range(8, 9)))
                .isInstanceOf(UnsupportedOperationException.class);
    }

    private static DateRange range(int from, int to) {
        return new DateRange(LocalDate.of(2026, 11, from), LocalDate.of(2026, 11, to));
    }
}
