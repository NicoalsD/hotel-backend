package com.hotel.reservations.repository;

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

class InMemoryReservationRepositoryTest {

    private InMemoryReservationRepository repository;
    private Room room201;
    private Room room101;
    private Guest guest;
    private DateRange range;

    @BeforeEach
    void setUp() {
        repository = new InMemoryReservationRepository();
        room201 = new Room(7L, "201", RoomType.DOUBLE, 2, "Doble");
        room101 = new Room(1L, "101", RoomType.SINGLE, 1, "Sencilla");
        guest = new Guest("Ana Pérez", "1020304050", "ana@correo.com", "3001234567");
        range = new DateRange(LocalDate.of(2026, 11, 1), LocalDate.of(2026, 11, 4));
    }

    private Reservation reservation(String id, Room room) {
        return new Reservation(id, guest, room, range, new BigDecimal("360.00"));
    }

    @Test
    void saveAndFindByIdReturnStoredReservation() {
        Reservation saved = repository.save(reservation("RES-1001", room201));

        assertThat(saved.getId()).isEqualTo("RES-1001");
        assertThat(repository.findById("RES-1001")).containsSame(saved);
    }

    @Test
    void findByIdReturnsEmptyWhenMissingOrNull() {
        assertThat(repository.findById("RES-9999")).isEmpty();
        assertThat(repository.findById(null)).isEmpty();
    }

    @Test
    void findAllReturnsNewestFirstWhenCreatedInSequence() {
        repository.save(reservation("RES-1001", room201));
        repository.save(reservation("RES-1002", room101));
        repository.save(reservation("RES-1003", room201));

        assertThat(repository.findAll())
                .extracting(Reservation::getId)
                .containsExactly("RES-1003", "RES-1002", "RES-1001");
    }

    @Test
    void findByRoomIdReturnsOnlyReservationsOfThatRoom() {
        repository.save(reservation("RES-1001", room201));
        repository.save(reservation("RES-1002", room101));
        repository.save(reservation("RES-1003", room201));

        assertThat(repository.findByRoomId(7L))
                .extracting(Reservation::getId)
                .containsExactly("RES-1003", "RES-1001");
        assertThat(repository.findByRoomId(99L)).isEmpty();
        assertThat(repository.findByRoomId(null)).isEmpty();
    }
}
