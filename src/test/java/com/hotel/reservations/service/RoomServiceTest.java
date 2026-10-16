package com.hotel.reservations.service;

import com.hotel.reservations.exception.ResourceNotFoundException;
import com.hotel.reservations.exception.RoomNotAvailableException;
import com.hotel.reservations.model.DateRange;
import com.hotel.reservations.model.Room;
import com.hotel.reservations.model.RoomStatus;
import com.hotel.reservations.model.RoomType;
import com.hotel.reservations.repository.InMemoryReservationRepository;
import com.hotel.reservations.repository.InMemoryRoomRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RoomServiceTest {

    private InMemoryRoomRepository roomRepository;
    private RoomService service;

    @BeforeEach
    void setUp() {
        roomRepository = new InMemoryRoomRepository();
        roomRepository.save(new Room(1L, "101", RoomType.SINGLE, 1, "Sencilla"));
        roomRepository.save(new Room(7L, "201", RoomType.DOUBLE, 2, "Doble"));
        roomRepository.save(new Room(8L, "202", RoomType.DOUBLE, 2, "Doble"));
        roomRepository.save(new Room(13L, "301", RoomType.SUITE, 3, "Suite"));
        service = new RoomService(roomRepository, new InMemoryReservationRepository());
    }

    private static DateRange range(String checkIn, String checkOut) {
        return new DateRange(LocalDate.parse(checkIn), LocalDate.parse(checkOut));
    }

    @Test
    void findAllReturnsEveryRoomSortedById() {
        assertThat(service.findAll()).extracting(Room::getId).containsExactly(1L, 7L, 8L, 13L);
    }

    @Test
    void findByIdReturnsRoomOrThrowsNotFound() {
        assertThat(service.findById(7L).getNumber()).isEqualTo("201");
        assertThatThrownBy(() -> service.findById(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("No se encontró la habitación 99")
                .satisfies(ex -> assertThat(((ResourceNotFoundException) ex).getStatusCode()).isEqualTo(404));
    }

    @Test
    void findByTypeWithNullReturnsAllRooms() {
        assertThat(service.findByType(null)).hasSize(4);
        assertThat(service.findByType(RoomType.DOUBLE))
                .extracting(Room::getNumber)
                .containsExactly("201", "202");
    }

    @Test
    void availabilityIsFalseWhenDatesOverlapBooking() {
        service.reserveRoom(7L, "RES-1001", range("2026-11-01", "2026-11-04"));

        assertThat(service.isRoomAvailable(7L, range("2026-11-03", "2026-11-05"))).isFalse();
        assertThat(service.isRoomAvailable(7L, range("2026-10-30", "2026-11-02"))).isFalse();
        assertThat(service.isRoomAvailable(7L, range("2026-11-02", "2026-11-03"))).isFalse();
    }

    @Test
    void availabilityIsTrueWhenRangesOnlyTouchAtBoundaries() {
        service.reserveRoom(7L, "RES-1001", range("2026-11-01", "2026-11-04"));

        assertThat(service.isRoomAvailable(7L, range("2026-11-04", "2026-11-06"))).isTrue();
        assertThat(service.isRoomAvailable(7L, range("2026-10-28", "2026-11-01"))).isTrue();
    }

    @Test
    void availabilityIsTrueForRoomWithoutBookings() {
        assertThat(service.isRoomAvailable(1L, range("2026-11-01", "2026-11-04"))).isTrue();
    }

    @Test
    void isRoomAvailableThrowsNotFoundForUnknownRoom() {
        assertThatThrownBy(() -> service.isRoomAvailable(99L, range("2026-11-01", "2026-11-04")))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void reserveRoomThrowsConflictWhenRangeOverlaps() {
        service.reserveRoom(7L, "RES-1001", range("2026-11-01", "2026-11-04"));

        assertThatThrownBy(() -> service.reserveRoom(7L, "RES-1002", range("2026-11-02", "2026-11-05")))
                .isInstanceOf(RoomNotAvailableException.class)
                .hasMessage("La habitación 201 no está disponible en las fechas seleccionadas")
                .satisfies(ex -> assertThat(((RoomNotAvailableException) ex).getStatusCode()).isEqualTo(409));
    }

    @Test
    void reserveRoomThrowsNotFoundForUnknownRoom() {
        assertThatThrownBy(() -> service.reserveRoom(99L, "RES-1001", range("2026-11-01", "2026-11-04")))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void releaseRoomRemovesBookingAndAllowsNewReservation() {
        service.reserveRoom(7L, "RES-1001", range("2026-11-01", "2026-11-04"));

        service.releaseRoom(7L, "RES-1001");

        assertThat(service.isRoomAvailable(7L, range("2026-11-01", "2026-11-04"))).isTrue();
        assertThat(service.findById(7L).getStatus()).isEqualTo(RoomStatus.AVAILABLE);
        service.reserveRoom(7L, "RES-1002", range("2026-11-01", "2026-11-04"));
        assertThat(service.isRoomAvailable(7L, range("2026-11-01", "2026-11-04"))).isFalse();
    }

    @Test
    void releaseRoomOnlyRemovesTheGivenReservation() {
        service.reserveRoom(7L, "RES-1001", range("2026-11-01", "2026-11-04"));
        service.reserveRoom(7L, "RES-1002", range("2026-11-10", "2026-11-12"));

        service.releaseRoom(7L, "RES-1001");

        assertThat(service.isRoomAvailable(7L, range("2026-11-01", "2026-11-04"))).isTrue();
        assertThat(service.isRoomAvailable(7L, range("2026-11-10", "2026-11-12"))).isFalse();
    }

    @Test
    void occupyRoomMarksRoomOccupied() {
        service.occupyRoom(7L);

        assertThat(service.findById(7L).getStatus()).isEqualTo(RoomStatus.OCCUPIED);
    }

    @Test
    void concurrentReservationsOfSameRoomAcceptExactlyOne() throws Exception {
        DateRange sameRange = range("2026-11-01", "2026-11-04");
        int attempts = 16;
        ExecutorService executor = Executors.newFixedThreadPool(attempts);
        CountDownLatch start = new CountDownLatch(1);
        List<Future<Boolean>> results = new ArrayList<>();
        try {
            for (int i = 0; i < attempts; i++) {
                String reservationId = "RES-" + (1001 + i);
                results.add(executor.submit(() -> {
                    start.await();
                    try {
                        service.reserveRoom(7L, reservationId, sameRange);
                        return true;
                    } catch (RoomNotAvailableException ex) {
                        return false;
                    }
                }));
            }
            start.countDown();

            long accepted = 0;
            for (Future<Boolean> result : results) {
                if (result.get()) {
                    accepted++;
                }
            }
            assertThat(accepted).isEqualTo(1);
            assertThat(service.isRoomAvailable(7L, sameRange)).isFalse();
        } finally {
            executor.shutdownNow();
        }
    }

    @Test
    void findAvailableRoomsExcludesBookedRoomsAndFiltersByType() {
        service.reserveRoom(7L, "RES-1001", range("2026-11-01", "2026-11-04"));

        assertThat(service.findAvailableRooms(range("2026-11-02", "2026-11-03"), RoomType.DOUBLE))
                .extracting(Room::getNumber)
                .containsExactly("202");
        assertThat(service.findAvailableRooms(range("2026-11-02", "2026-11-03"), null))
                .extracting(Room::getId)
                .containsExactly(1L, 8L, 13L);
        assertThat(service.findAvailableRooms(range("2026-12-01", "2026-12-03"), RoomType.DOUBLE))
                .extracting(Room::getNumber)
                .containsExactly("201", "202");
    }
}
