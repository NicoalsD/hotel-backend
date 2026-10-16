package com.hotel.reservations.model;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Línea de una factura: un cargo por hospedaje o por servicio.
 */
public class InvoiceLine {

    private final String description;
    private final InvoiceLineCategory category;
    private final long quantity;
    private final BigDecimal unitPrice;

    /**
     * @param description descripción en español del cargo
     * @param category categoría del cargo (ROOM o SERVICE)
     * @param quantity cantidad facturada
     * @param unitPrice precio unitario
     */
    public InvoiceLine(String description, InvoiceLineCategory category, long quantity, BigDecimal unitPrice) {
        this.description = description;
        this.category = category;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
    }

    /** @return importe de la línea (precio unitario por cantidad), con escala 2 */
    public BigDecimal getAmount() {
        return unitPrice.multiply(BigDecimal.valueOf(quantity)).setScale(2, RoundingMode.HALF_UP);
    }

    /** @return descripción del cargo */
    public String getDescription() {
        return description;
    }

    /** @return categoría del cargo */
    public InvoiceLineCategory getCategory() {
        return category;
    }

    /** @return cantidad facturada */
    public long getQuantity() {
        return quantity;
    }

    /** @return precio unitario */
    public BigDecimal getUnitPrice() {
        return unitPrice;
    }
}
