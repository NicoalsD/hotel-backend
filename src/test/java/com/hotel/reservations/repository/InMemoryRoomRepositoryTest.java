package com.hotel.reservations.repository;

import com.hotel.reservations.model.Room;
import com.hotel.reservations.model.RoomType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class InMemoryRoomRepositoryTest {

    private InMemoryRoomRepository repository;

    @BeforeEach
    void setUp() {
        repository = new InMemoryRoomRepository();
        repository.save(new Room(7L, "201", RoomType.DOUBLE, 2, "Doble"));
        repository.save(new Room(1L, "101", RoomType.SINGLE, 1, "Sencilla"));
        repository.save(new Room(13L, "301", RoomType.SUITE, 3, "Suite"));
        repository.save(new Room(8L, "202", RoomType.DOUBLE, 2, "Doble"));
    }

    @Test
    void findAllReturnsRoomsSortedById() {
        List<Room> rooms = repository.findAll();

        assertThat(rooms).extracting(Room::getId).containsExactly(1L, 7L, 8L, 13L);
    }

    @Test
    void findByIdReturnsStoredRoom() {
        assertThat(repository.findById(7L)).isPresent()
                .get().extracting(Room::getNumber).isEqualTo("201");
    }

    @Test
    void findByIdReturnsEmptyWhenMissingOrNull() {
        assertThat(repository.findById(99L)).isEmpty();
        assertThat(repository.findById(null)).isEmpty();
    }

    @Test
    void findByTypeFiltersAndKeepsIdOrder() {
        assertThat(repository.findByType(RoomType.DOUBLE))
                .extracting(Room::getNumber)
                .containsExactly("201", "202");
    }

    @Test
    void countReflectsSavedRooms() {
        assertThat(repository.count()).isEqualTo(4);
    }

    @Test
    void saveReplacesRoomWithSameId() {
        repository.save(new Room(1L, "101", RoomType.SINGLE, 1, "Actualizada"));

        assertThat(repository.count()).isEqualTo(4);
        assertThat(repository.findById(1L).orElseThrow().getDescription()).isEqualTo("Actualizada");
    }
}
