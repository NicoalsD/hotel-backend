package com.hotel.reservations.config;

import com.hotel.reservations.model.Room;
import com.hotel.reservations.model.RoomType;
import com.hotel.reservations.repository.InMemoryRoomRepository;
import org.junit.jupiter.api.Test;
import org.springframework.boot.DefaultApplicationArguments;

import java.util.List;
import java.util.stream.LongStream;

import static org.assertj.core.api.Assertions.assertThat;

class RoomSeederTest {

    @Test
    void seedsSixteenRoomsOnEmptyRepository() throws Exception {
        InMemoryRoomRepository repository = new InMemoryRoomRepository();

        new RoomSeeder(repository).run(new DefaultApplicationArguments());

        assertThat(repository.count()).isEqualTo(16);
        assertThat(repository.findAll()).extracting(Room::getId)
                .containsExactlyElementsOf(LongStream.rangeClosed(1, 16).boxed().toList());
    }

    @Test
    void splitsRoomsBySixSixAndFourTypes() throws Exception {
        InMemoryRoomRepository repository = new InMemoryRoomRepository();

        new RoomSeeder(repository).run(new DefaultApplicationArguments());

        assertThat(repository.findByType(RoomType.SINGLE)).hasSize(6);
        assertThat(repository.findByType(RoomType.DOUBLE)).hasSize(6);
        assertThat(repository.findByType(RoomType.SUITE)).hasSize(4);
    }

    @Test
    void roomNumbersFloorsAndIdsMatchContract() {
        List<Room> rooms = RoomSeeder.buildInventory();

        assertThat(rooms.get(0).getNumber()).isEqualTo("101");
        assertThat(rooms.get(0).getFloor()).isEqualTo(1);
        assertThat(rooms.get(5).getNumber()).isEqualTo("106");
        assertThat(rooms.get(6).getId()).isEqualTo(7L);
        assertThat(rooms.get(6).getNumber()).isEqualTo("201");
        assertThat(rooms.get(6).getFloor()).isEqualTo(2);
        assertThat(rooms.get(12).getNumber()).isEqualTo("301");
        assertThat(rooms.get(12).getFloor()).isEqualTo(3);
        assertThat(rooms.get(15).getNumber()).isEqualTo("304");
        assertThat(rooms.get(15).getType()).isEqualTo(RoomType.SUITE);
    }

    @Test
    void everyRoomHasSpanishDescription() {
        assertThat(RoomSeeder.buildInventory())
                .allSatisfy(room -> assertThat(room.getDescription()).isNotBlank());
        assertThat(RoomSeeder.buildInventory().get(6).getDescription())
                .isEqualTo("Habitación doble con vista a la ciudad");
    }

    @Test
    void doesNotDuplicateRoomsWhenRunTwice() throws Exception {
        InMemoryRoomRepository repository = new InMemoryRoomRepository();
        RoomSeeder seeder = new RoomSeeder(repository);

        seeder.run(new DefaultApplicationArguments());
        seeder.run(new DefaultApplicationArguments());

        assertThat(repository.count()).isEqualTo(16);
    }

    @Test
    void keepsExistingRoomsWhenRepositoryIsNotEmpty() throws Exception {
        InMemoryRoomRepository repository = new InMemoryRoomRepository();
        repository.save(new Room(99L, "999", RoomType.SUITE, 9, "Existente"));

        new RoomSeeder(repository).run(new DefaultApplicationArguments());

        assertThat(repository.count()).isEqualTo(1);
    }
}
