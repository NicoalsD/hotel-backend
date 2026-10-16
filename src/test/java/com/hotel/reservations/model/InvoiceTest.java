package com.hotel.reservations.model;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class InvoiceTest {

    private Invoice buildInvoice(List<InvoiceLine> lines) {
        Reservation reservation = new Reservation("RES-1001",
                new Guest("Ana Pérez", "1020304050", "ana@correo.com", "3001234567"),
                new Room(7L, "201", RoomType.DOUBLE, 2, "Habitación doble"),
                new DateRange(LocalDate.of(2026, 11, 1), LocalDate.of(2026, 11, 4)),
                new BigDecimal("360.00"));
        return new Invoice("FAC-0001", reservation, lines, new BigDecimal("0.19"),
                LocalDateTime.of(2026, 11, 4, 12, 0));
    }

    private List<InvoiceLine> contractLines() {
        return List.of(
                new InvoiceLine("Habitación 201 (Doble) - Temporada baja",
                        InvoiceLineCategory.ROOM, 3, new BigDecimal("120.00")),
                new InvoiceLine("Desayuno", InvoiceLineCategory.SERVICE, 3, new BigDecimal("15.00")));
    }

    @Test
    void calculatesSubtotalsTaxAndTotalAsInContract() {
        Invoice invoice = buildInvoice(contractLines());

        assertThat(invoice.getRoomSubtotal()).isEqualTo(new BigDecimal("360.00"));
        assertThat(invoice.getServicesSubtotal()).isEqualTo(new BigDecimal("45.00"));
        assertThat(invoice.getSubtotal()).isEqualTo(new BigDecimal("405.00"));
        assertThat(invoice.getTaxAmount()).isEqualTo(new BigDecimal("76.95"));
        assertThat(invoice.getTotal()).isEqualTo(new BigDecimal("481.95"));
    }

    @Test
    void invoiceWithoutServicesHasZeroServicesSubtotal() {
        Invoice invoice = buildInvoice(List.of(contractLines().get(0)));

        assertThat(invoice.getServicesSubtotal()).isEqualTo(new BigDecimal("0.00"));
        assertThat(invoice.getSubtotal()).isEqualTo(new BigDecimal("360.00"));
    }

    @Test
    void taxRoundsHalfUpToTwoDecimals() {
        Invoice invoice = new Invoice("FAC-0002", buildInvoice(contractLines()).getReservation(),
                List.of(new InvoiceLine("Ajuste", InvoiceLineCategory.ROOM, 1, new BigDecimal("0.05"))),
                new BigDecimal("0.19"), LocalDateTime.now());

        assertThat(invoice.getTaxAmount()).isEqualTo(new BigDecimal("0.01"));
        assertThat(invoice.getTotal()).isEqualTo(new BigDecimal("0.06"));
    }

    @Test
    void exposesIdentityAndLinesReadOnly() {
        Invoice invoice = buildInvoice(contractLines());

        assertThat(invoice.getInvoiceNumber()).isEqualTo("FAC-0001");
        assertThat(invoice.getReservation().getId()).isEqualTo("RES-1001");
        assertThat(invoice.getTaxRate()).isEqualTo(new BigDecimal("0.19"));
        assertThat(invoice.getIssuedAt()).isEqualTo(LocalDateTime.of(2026, 11, 4, 12, 0));
        assertThat(invoice.getLines()).hasSize(2);
        assertThatThrownBy(() -> invoice.getLines().clear())
                .isInstanceOf(UnsupportedOperationException.class);
    }
}
