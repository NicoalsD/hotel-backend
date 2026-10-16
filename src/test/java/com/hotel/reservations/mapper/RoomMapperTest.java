package com.hotel.reservations.mapper;

import com.hotel.reservations.dto.RoomAvailabilityDTO;
import com.hotel.reservations.dto.RoomDTO;
import com.hotel.reservations.model.DateRange;
import com.hotel.reservations.model.PriceQuote;
import com.hotel.reservations.model.Room;
import com.hotel.reservations.model.RoomStatus;
import com.hotel.reservations.model.RoomType;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

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

    @Test
    void convierteDisponibilidadConNochesYTotalDeCotizacion() {
        Room room = new Room(7L, "201", RoomType.DOUBLE, 2, "Habitación doble");
        DateRange range = new DateRange(LocalDate.of(2026, 11, 1), LocalDate.of(2026, 11, 4));
        PriceQuote quote = new PriceQuote(RoomType.DOUBLE, range, List.of());

        RoomAvailabilityDTO dto = mapper.toAvailabilityDto(room, true, quote);

        assertThat(dto.roomId()).isEqualTo(7L);
        assertThat(dto.number()).isEqualTo("201");
        assertThat(dto.typeLabel()).isEqualTo("Doble");
        assertThat(dto.basePrice()).isEqualTo(new BigDecimal("120.00"));
        assertThat(dto.available()).isTrue();
        assertThat(dto.nights()).isZero();
        assertThat(dto.estimatedTotal()).isEqualTo(new BigDecimal("0.00"));
    }

    @Test
    void marcaHabitacionNoDisponibleYUsaNochesDeLaCotizacion() {
        Room room = new Room(8L, "202", RoomType.DOUBLE, 2, "Habitación doble");
        DateRange range = new DateRange(LocalDate.of(2026, 11, 1), LocalDate.of(2026, 11, 4));
        PriceQuote quote = new PriceQuote(RoomType.DOUBLE, range, List.of(
                new com.hotel.reservations.model.NightlyRate(LocalDate.of(2026, 11, 1),
                        com.hotel.reservations.model.Season.LOW, new BigDecimal("120.00")),
                new com.hotel.reservations.model.NightlyRate(LocalDate.of(2026, 11, 2),
                        com.hotel.reservations.model.Season.LOW, new BigDecimal("120.00")),
                new com.hotel.reservations.model.NightlyRate(LocalDate.of(2026, 11, 3),
                        com.hotel.reservations.model.Season.LOW, new BigDecimal("120.00"))));

        RoomAvailabilityDTO dto = mapper.toAvailabilityDto(room, false, quote);

        assertThat(dto.available()).isFalse();
        assertThat(dto.nights()).isEqualTo(3);
        assertThat(dto.estimatedTotal()).isEqualTo(new BigDecimal("360.00"));
    }

    @Test
    void rechazaCotizacionNula() {
        Room room = new Room(7L, "201", RoomType.DOUBLE, 2, "Habitación doble");

        assertThatThrownBy(() -> mapper.toAvailabilityDto(room, true, null))
                .isInstanceOf(NullPointerException.class);
    }
}
