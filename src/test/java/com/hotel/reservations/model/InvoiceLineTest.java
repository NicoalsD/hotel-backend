package com.hotel.reservations.model;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class InvoiceLineTest {

    @Test
    void amountIsUnitPriceTimesQuantityWithScaleTwo() {
        InvoiceLine line = new InvoiceLine("Habitación 201 (Doble) - Temporada baja",
                InvoiceLineCategory.ROOM, 3, new BigDecimal("120"));

        assertThat(line.getAmount()).isEqualTo(new BigDecimal("360.00"));
    }

    @Test
    void amountRoundsHalfUpToTwoDecimals() {
        InvoiceLine line = new InvoiceLine("Ajuste", InvoiceLineCategory.SERVICE, 3,
                new BigDecimal("0.005"));

        assertThat(line.getAmount()).isEqualTo(new BigDecimal("0.02"));
    }

    @Test
    void exposesDescriptionCategoryQuantityAndUnitPrice() {
        InvoiceLine line = new InvoiceLine("Desayuno", InvoiceLineCategory.SERVICE, 3,
                new BigDecimal("15.00"));

        assertThat(line.getDescription()).isEqualTo("Desayuno");
        assertThat(line.getCategory()).isEqualTo(InvoiceLineCategory.SERVICE);
        assertThat(line.getQuantity()).isEqualTo(3);
        assertThat(line.getUnitPrice()).isEqualTo(new BigDecimal("15.00"));
        assertThat(line.getAmount()).isEqualTo(new BigDecimal("45.00"));
    }
}
