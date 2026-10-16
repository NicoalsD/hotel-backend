package com.hotel.reservations.repository;

import com.hotel.reservations.model.DateRange;
import com.hotel.reservations.model.Guest;
import com.hotel.reservations.model.Invoice;
import com.hotel.reservations.model.InvoiceLine;
import com.hotel.reservations.model.InvoiceLineCategory;
import com.hotel.reservations.model.Reservation;
import com.hotel.reservations.model.Room;
import com.hotel.reservations.model.RoomType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class InMemoryInvoiceRepositoryTest {

    private InMemoryInvoiceRepository repository;
    private Reservation reservation1001;
    private Reservation reservation1002;

    @BeforeEach
    void setUp() {
        repository = new InMemoryInvoiceRepository();
        Guest guest = new Guest("Ana Pérez", "1020304050", "ana@correo.com", "3001234567");
        Room room = new Room(7L, "201", RoomType.DOUBLE, 2, "Doble");
        DateRange range = new DateRange(LocalDate.of(2026, 11, 1), LocalDate.of(2026, 11, 4));
        reservation1001 = new Reservation("RES-1001", guest, room, range, new BigDecimal("360.00"));
        reservation1002 = new Reservation("RES-1002", guest, room, range, new BigDecimal("360.00"));
    }

    private Invoice invoice(String number, Reservation reservation) {
        List<InvoiceLine> lines = List.of(
                new InvoiceLine("Habitación 201 (Doble) - Temporada baja", InvoiceLineCategory.ROOM, 3,
                        new BigDecimal("120.00")));
        return new Invoice(number, reservation, lines, new BigDecimal("0.19"), LocalDateTime.now());
    }

    @Test
    void saveAndFindByNumberReturnStoredInvoice() {
        Invoice saved = repository.save(invoice("FAC-0001", reservation1001));

        assertThat(repository.findByNumber("FAC-0001")).containsSame(saved);
        assertThat(repository.findByNumber("FAC-9999")).isEmpty();
        assertThat(repository.findByNumber(null)).isEmpty();
    }

    @Test
    void findByReservationIdReturnsInvoiceOfThatReservation() {
        repository.save(invoice("FAC-0001", reservation1001));
        repository.save(invoice("FAC-0002", reservation1002));

        assertThat(repository.findByReservationId("RES-1002"))
                .get()
                .extracting(Invoice::getInvoiceNumber)
                .isEqualTo("FAC-0002");
        assertThat(repository.findByReservationId("RES-9999")).isEmpty();
        assertThat(repository.findByReservationId(null)).isEmpty();
    }

    @Test
    void findAllReturnsInvoicesSortedByNumber() {
        repository.save(invoice("FAC-0002", reservation1002));
        repository.save(invoice("FAC-0001", reservation1001));

        assertThat(repository.findAll())
                .extracting(Invoice::getInvoiceNumber)
                .containsExactly("FAC-0001", "FAC-0002");
    }
}
