package com.hotel.reservations.mapper;

import com.hotel.reservations.dto.InvoiceDTO;
import com.hotel.reservations.dto.InvoiceLineDTO;
import com.hotel.reservations.model.AdditionalServiceType;
import com.hotel.reservations.model.DateRange;
import com.hotel.reservations.model.DigitalKey;
import com.hotel.reservations.model.Guest;
import com.hotel.reservations.model.Invoice;
import com.hotel.reservations.model.InvoiceLine;
import com.hotel.reservations.model.InvoiceLineCategory;
import com.hotel.reservations.model.Reservation;
import com.hotel.reservations.model.Room;
import com.hotel.reservations.model.RoomType;
import com.hotel.reservations.support.StubAdditionalService;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class InvoiceMapperTest {

    private final InvoiceMapper mapper = new InvoiceMapper(new GuestMapper());

    private Invoice invoiceFor(Reservation reservation) {
        List<InvoiceLine> lines = List.of(
                new InvoiceLine("Habitación 201 (Doble) - Temporada baja", InvoiceLineCategory.ROOM, 3,
                        new BigDecimal("120.00")),
                new InvoiceLine("Desayuno", InvoiceLineCategory.SERVICE, 3, new BigDecimal("15.00")));
        return new Invoice("FAC-0001", reservation, lines, new BigDecimal("0.19"),
                LocalDateTime.of(2026, 11, 4, 11, 0));
    }

    private Reservation reservationWithService() {
        Guest guest = new Guest("Ana Pérez", "1020304050", "ana@correo.com", "3001234567");
        Room room = new Room(8L, "201", RoomType.DOUBLE, 2, "Habitación doble");
        DateRange range = new DateRange(LocalDate.of(2026, 11, 1), LocalDate.of(2026, 11, 4));
        Reservation reservation = new Reservation("RES-1001", guest, room, range, new BigDecimal("360.00"));
        reservation.addService(new StubAdditionalService("SRV-1", AdditionalServiceType.BREAKFAST, 3));
        reservation.checkIn(new DigitalKey("key-abc", "201", "RES-1001", LocalDateTime.of(2026, 11, 1, 15, 0)));
        reservation.checkOut("FAC-0001");
        return reservation;
    }

    @Test
    void convierteFacturaConLineasYTotales() {
        InvoiceDTO dto = mapper.toDto(invoiceFor(reservationWithService()));

        assertThat(dto.invoiceNumber()).isEqualTo("FAC-0001");
        assertThat(dto.reservationId()).isEqualTo("RES-1001");
        assertThat(dto.issuedAt()).isEqualTo(LocalDateTime.of(2026, 11, 4, 11, 0));
        assertThat(dto.guest().documentNumber()).isEqualTo("1020304050");
        assertThat(dto.roomNumber()).isEqualTo("201");
        assertThat(dto.roomType()).isEqualTo(RoomType.DOUBLE);
        assertThat(dto.roomTypeLabel()).isEqualTo("Doble");
        assertThat(dto.checkIn()).isEqualTo(LocalDate.of(2026, 11, 1));
        assertThat(dto.checkOut()).isEqualTo(LocalDate.of(2026, 11, 4));
        assertThat(dto.nights()).isEqualTo(3);
        assertThat(dto.lines()).containsExactly(
                new InvoiceLineDTO("Habitación 201 (Doble) - Temporada baja", InvoiceLineCategory.ROOM, 3,
                        new BigDecimal("120.00"), new BigDecimal("360.00")),
                new InvoiceLineDTO("Desayuno", InvoiceLineCategory.SERVICE, 3,
                        new BigDecimal("15.00"), new BigDecimal("45.00")));
        assertThat(dto.roomSubtotal()).isEqualTo(new BigDecimal("360.00"));
        assertThat(dto.servicesSubtotal()).isEqualTo(new BigDecimal("45.00"));
        assertThat(dto.subtotal()).isEqualTo(new BigDecimal("405.00"));
        assertThat(dto.taxRate()).isEqualTo(new BigDecimal("0.19"));
        assertThat(dto.taxAmount()).isEqualTo(new BigDecimal("76.95"));
        assertThat(dto.total()).isEqualTo(new BigDecimal("481.95"));
        assertThat(dto.digitalKeyCode()).isEqualTo("key-abc");
    }

    @Test
    void digitalKeyCodeEsNuloSiLaReservaNoTuvoLlave() {
        Guest guest = new Guest("Luis Gómez", "900800700", "luis@correo.com", "3109876543");
        Room room = new Room(1L, "101", RoomType.SINGLE, 1, "Habitación sencilla");
        Reservation reservation = new Reservation("RES-1002", guest, room,
                new DateRange(LocalDate.of(2026, 11, 1), LocalDate.of(2026, 11, 2)), new BigDecimal("80.00"));
        List<InvoiceLine> lines = List.of(
                new InvoiceLine("Habitación 101 (Sencilla) - Temporada baja", InvoiceLineCategory.ROOM, 1,
                        new BigDecimal("80.00")));
        Invoice invoice = new Invoice("FAC-0002", reservation, lines, new BigDecimal("0.19"),
                LocalDateTime.of(2026, 11, 2, 10, 0));

        InvoiceDTO dto = mapper.toDto(invoice);

        assertThat(dto.digitalKeyCode()).isNull();
        assertThat(dto.servicesSubtotal()).isEqualTo(new BigDecimal("0.00"));
    }
}
