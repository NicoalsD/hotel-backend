package com.hotel.reservations.config;

import com.hotel.reservations.model.Room;
import com.hotel.reservations.model.RoomType;
import com.hotel.reservations.repository.RoomRepository;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Carga al inicio de la aplicación el inventario de 16 habitaciones del hotel:
 * 6 sencillas (ids 1-6, números 101-106, piso 1), 6 dobles (ids 7-12, números 201-206, piso 2)
 * y 4 suites (ids 13-16, números 301-304, piso 3).
 * Solo siembra datos cuando el repositorio de habitaciones está vacío.
 */
@Component
public class RoomSeeder implements ApplicationRunner {

    private static final String[] DESCRIPTIONS = {
            "Habitación sencilla interior con cama individual",
            "Habitación sencilla con escritorio y vista al jardín",
            "Habitación sencilla con baño privado y ducha",
            "Habitación sencilla silenciosa ideal para viajeros de negocios",
            "Habitación sencilla con ventana amplia y luz natural",
            "Habitación sencilla económica con wifi y televisor",
            "Habitación doble con vista a la ciudad",
            "Habitación doble con dos camas y minibar",
            "Habitación doble con balcón privado",
            "Habitación doble con cama queen y escritorio",
            "Habitación doble con baño de mármol",
            "Habitación doble con vista al parque",
            "Suite con sala de estar y vista panorámica",
            "Suite con jacuzzi y cama king size",
            "Suite familiar con dos habitaciones comunicadas",
            "Suite ejecutiva con comedor privado y terraza"
    };

    private final RoomRepository roomRepository;

    /**
     * @param roomRepository repositorio donde se guardan las habitaciones
     */
    public RoomSeeder(RoomRepository roomRepository) {
        this.roomRepository = roomRepository;
    }

    /**
     * Siembra las habitaciones solo si el repositorio está vacío.
     *
     * @param args argumentos de la aplicación (no se usan)
     */
    @Override
    public void run(ApplicationArguments args) {
        if (roomRepository.count() > 0) {
            return;
        }
        for (Room room : buildInventory()) {
            roomRepository.save(room);
        }
    }

    /**
     * @return las 16 habitaciones del inventario según el contrato de la API:
     *     6 sencillas (101-106), 6 dobles (201-206) y 4 suites (301-304)
     */
    static List<Room> buildInventory() {
        List<Room> rooms = new ArrayList<>();
        long id = 1L;
        for (int i = 0; i < 6; i++) {
            rooms.add(new Room(id, "10" + (i + 1), RoomType.SINGLE, 1, DESCRIPTIONS[(int) id - 1]));
            id++;
        }
        for (int i = 0; i < 6; i++) {
            rooms.add(new Room(id, "20" + (i + 1), RoomType.DOUBLE, 2, DESCRIPTIONS[(int) id - 1]));
            id++;
        }
        for (int i = 0; i < 4; i++) {
            rooms.add(new Room(id, "30" + (i + 1), RoomType.SUITE, 3, DESCRIPTIONS[(int) id - 1]));
            id++;
        }
        return rooms;
    }
}
