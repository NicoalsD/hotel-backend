package com.hotel.reservations.mapper;

import com.hotel.reservations.dto.RoomDTO;
import com.hotel.reservations.model.Room;
import com.hotel.reservations.model.RoomStatus;
import com.hotel.reservations.model.RoomType;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class RoomMapperTest {

    private final RoomMapper mapper = new RoomMapper();

    @Test
    void convierteHabitacionADtoConEtiquetas() {
        Room room = new Room(7L, "201", RoomType.DOUBLE, 2, "Habitación doble con vista a la ciudad");

        RoomDTO dto = mapper.toDto(room);

        assertThat(dto.id()).isEqualTo(7L);
        assertThat(dto.number()).isEqualTo("201");
        assertThat(dto.type()).isEqualTo(RoomType.DOUBLE);
        assertThat(dto.typeLabel()).isEqualTo("Doble");
        assertThat(dto.floor()).isEqualTo(2);
        assertThat(dto.capacity()).isEqualTo(2);
        assertThat(dto.basePrice()).isEqualTo(new BigDecimal("120.00"));
        assertThat(dto.description()).isEqualTo("Habitación doble con vista a la ciudad");
        assertThat(dto.status()).isEqualTo(RoomStatus.AVAILABLE);
        assertThat(dto.statusLabel()).isEqualTo("Disponible");
    }

    @Test
    void reflejaEstadoOcupada() {
        Room room = new Room(1L, "101", RoomType.SINGLE, 1, "Habitación sencilla");
        room.markOccupied();

        RoomDTO dto = mapper.toDto(room);

        assertThat(dto.status()).isEqualTo(RoomStatus.OCCUPIED);
        assertThat(dto.statusLabel()).isEqualTo("Ocupada");
    }
}
