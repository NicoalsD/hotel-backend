package com.hotel.reservations.mapper;

import com.hotel.reservations.dto.ReservationDTO;
import com.hotel.reservations.model.DateRange;
import com.hotel.reservations.model.Guest;
import com.hotel.reservations.model.Reservation;
import com.hotel.reservations.model.ReservationStatus;
import com.hotel.reservations.model.Room;
import com.hotel.reservations.model.RoomType;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

class ReservationMapperTest {

    private final ReservationMapper mapper = new ReservationMapper(new RoomMapper(), new GuestMapper());

    private Reservation confirmedReservation() {
        Guest guest = new Guest("Ana Pérez", "1020304050", "ana@correo.com", "3001234567");
        Room room = new Room(1L, "101", RoomType.SINGLE, 1, "Habitación sencilla");
        DateRange range = new DateRange(LocalDate.of(2026, 11, 1), LocalDate.of(2026, 11, 4));
        return new Reservation("RES-1001", guest, room, range, new BigDecimal("240.00"));
    }

    @Test
    void convierteReservaConfirmadaSinServiciosNiLlave() {
        ReservationDTO dto = mapper.toDto(confirmedReservation());

        assertThat(dto.id()).isEqualTo("RES-1001");
        assertThat(dto.status()).isEqualTo(ReservationStatus.CONFIRMED);
        assertThat(dto.statusLabel()).isEqualTo("Confirmada");
        assertThat(dto.guest().fullName()).isEqualTo("Ana Pérez");
        assertThat(dto.room().number()).isEqualTo("101");
        assertThat(dto.checkIn()).isEqualTo(LocalDate.of(2026, 11, 1));
        assertThat(dto.checkOut()).isEqualTo(LocalDate.of(2026, 11, 4));
        assertThat(dto.nights()).isEqualTo(3);
        assertThat(dto.roomTotal()).isEqualTo(new BigDecimal("240.00"));
        assertThat(dto.services()).isEmpty();
        assertThat(dto.servicesTotal()).isEqualTo(new BigDecimal("0.00"));
        assertThat(dto.estimatedTotal()).isEqualTo(new BigDecimal("240.00"));
        assertThat(dto.digitalKey()).isNull();
        assertThat(dto.invoiceNumber()).isNull();
        assertThat(dto.checkedInAt()).isNull();
        assertThat(dto.checkedOutAt()).isNull();
    }
}
