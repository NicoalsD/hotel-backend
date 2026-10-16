package com.hotel.reservations.repository.postgres;

import com.hotel.reservations.model.Room;
import com.hotel.reservations.model.RoomType;
import com.hotel.reservations.repository.RoomRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Repositorio de habitaciones con PostgreSQL. Las habitaciones viven en un {@link ConcurrentHashMap}
 * (estado de trabajo) y cada {@link #save(Room)} se escribe en la base de datos (write-through).
 */
@Repository
@Profile("postgres")
public class PostgresRoomRepository implements RoomRepository {

    private static final String UPSERT_SQL = """
            INSERT INTO rooms (id, number, type, floor, description, status)
            VALUES (?, ?, ?, ?, ?, ?)
            ON CONFLICT (id) DO UPDATE SET number = EXCLUDED.number, type = EXCLUDED.type,
                floor = EXCLUDED.floor, description = EXCLUDED.description, status = EXCLUDED.status
            """;

    private final Map<Long, Room> rooms = new ConcurrentHashMap<>();
    private final JdbcTemplate jdbcTemplate;

    /**
     * @param jdbcTemplate acceso JDBC a PostgreSQL
     */
    public PostgresRoomRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * Carga todas las habitaciones persistidas en la caché. Se usa al arrancar.
     *
     * @return habitaciones cargadas, ordenadas por identificador
     */
    public List<Room> loadAll() {
        List<Room> loaded = jdbcTemplate.query("SELECT * FROM rooms", new RoomRowMapper());
        loaded.forEach(room -> rooms.put(room.getId(), room));
        return findAll();
    }

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
        Long id = Objects.requireNonNull(room.getId(), "La habitación debe tener identificador");
        jdbcTemplate.update(UPSERT_SQL, id, room.getNumber(), room.getType().name(), room.getFloor(),
                room.getDescription(), room.getStatus().name());
        rooms.put(id, room);
        return room;
    }

    @Override
    public long count() {
        return rooms.size();
    }
}
