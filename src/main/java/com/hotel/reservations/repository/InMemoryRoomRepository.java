package com.hotel.reservations.repository;

import com.hotel.reservations.model.Room;
import com.hotel.reservations.model.RoomType;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Repositorio de habitaciones en memoria, respaldado por un {@link ConcurrentHashMap}.
 */
@Repository
@Profile("!postgres")
public class InMemoryRoomRepository implements RoomRepository {

    private final Map<Long, Room> rooms = new ConcurrentHashMap<>();

    @Override
    public List<Room> findAll() {
        return rooms.values().stream()
                .sorted(Comparator.comparing(Room::getId))
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Room> findById(Long id) {
        if (id == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(rooms.get(id));
    }

    @Override
    public List<Room> findByType(RoomType type) {
        return rooms.values().stream()
                .filter(room -> room.getType() == type)
                .sorted(Comparator.comparing(Room::getId))
                .collect(Collectors.toList());
    }

    @Override
    public Room save(Room room) {
        rooms.put(Objects.requireNonNull(room.getId(), "La habitación debe tener identificador"), room);
        return room;
    }

    @Override
    public long count() {
        return rooms.size();
    }
}
