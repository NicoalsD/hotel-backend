package com.hotel.reservations.mapper;

import com.hotel.reservations.dto.AdditionalServiceDTO;
import com.hotel.reservations.dto.DigitalKeyDTO;
import com.hotel.reservations.dto.ReservationDTO;
import com.hotel.reservations.model.AdditionalServiceType;
import com.hotel.reservations.model.ChargeMode;
import com.hotel.reservations.model.DateRange;
import com.hotel.reservations.model.DigitalKey;
import com.hotel.reservations.model.Guest;
import com.hotel.reservations.model.Reservation;
import com.hotel.reservations.model.ReservationStatus;
import com.hotel.reservations.model.Room;
import com.hotel.reservations.model.RoomType;
import com.hotel.reservations.support.StubAdditionalService;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

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

    @Test
    void incluyeServiciosTotalesYLlaveDigitalTrasCheckIn() {
        Reservation reservation = confirmedReservation();
        reservation.addService(new StubAdditionalService("SRV-1", AdditionalServiceType.BREAKFAST, 3));
        reservation.addService(new StubAdditionalService("SRV-2", AdditionalServiceType.SPA, 1));
        LocalDateTime issued = LocalDateTime.of(2026, 11, 1, 15, 0);
        reservation.checkIn(new DigitalKey("key-123", "101", "RES-1001", issued));

        ReservationDTO dto = mapper.toDto(reservation);

        assertThat(dto.status()).isEqualTo(ReservationStatus.CHECKED_IN);
        assertThat(dto.statusLabel()).isEqualTo("Check-in realizado");
        assertThat(dto.checkedInAt()).isNotNull();
        assertThat(dto.services()).hasSize(2);
        AdditionalServiceDTO breakfast = dto.services().get(0);
        assertThat(breakfast.id()).isEqualTo("SRV-1");
        assertThat(breakfast.type()).isEqualTo(AdditionalServiceType.BREAKFAST);
        assertThat(breakfast.chargeMode()).isEqualTo(ChargeMode.PER_NIGHT);
        assertThat(breakfast.unitPrice()).isEqualTo(new BigDecimal("15.00"));
        assertThat(breakfast.quantity()).isEqualTo(3);
        assertThat(breakfast.total()).isEqualTo(new BigDecimal("45.00"));
        assertThat(breakfast.addedAt()).isEqualTo(reservation.getServices().get(0).getAddedAt());
        assertThat(dto.servicesTotal()).isEqualTo(new BigDecimal("95.00"));
        assertThat(dto.estimatedTotal()).isEqualTo(new BigDecimal("335.00"));
        assertThat(dto.digitalKey()).isEqualTo(new DigitalKeyDTO("key-123", "101", issued, true));
    }
}
