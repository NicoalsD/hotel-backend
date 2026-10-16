package com.hotel.reservations.model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Factura emitida al hacer check-out. Calcula subtotales, IVA y total a partir de sus líneas.
 */
public class Invoice {

    private final String invoiceNumber;
    private final Reservation reservation;
    private final List<InvoiceLine> lines;
    private final BigDecimal taxRate;
    private final LocalDateTime issuedAt;

    /**
     * @param invoiceNumber número de factura (por ejemplo "FAC-0001")
     * @param reservation reserva facturada
     * @param lines líneas de cargo
     * @param taxRate tasa de IVA aplicada (por ejemplo 0.19)
     * @param issuedAt fecha y hora de emisión
     */
    public Invoice(String invoiceNumber, Reservation reservation, List<InvoiceLine> lines,
                   BigDecimal taxRate, LocalDateTime issuedAt) {
        this.invoiceNumber = invoiceNumber;
        this.reservation = reservation;
        this.lines = new ArrayList<>(lines);
        this.taxRate = taxRate;
        this.issuedAt = issuedAt;
    }

    /** @return suma de las líneas de categoría ROOM */
    public BigDecimal getRoomSubtotal() {
        return sumByCategory(InvoiceLineCategory.ROOM);
    }

    /** @return suma de las líneas de categoría SERVICE */
    public BigDecimal getServicesSubtotal() {
        return sumByCategory(InvoiceLineCategory.SERVICE);
    }

    /** @return subtotal antes de impuestos, con escala 2 */
    public BigDecimal getSubtotal() {
        return getRoomSubtotal().add(getServicesSubtotal()).setScale(2, RoundingMode.HALF_UP);
    }

    /** @return IVA calculado sobre el subtotal, con escala 2 */
    public BigDecimal getTaxAmount() {
        return getSubtotal().multiply(taxRate).setScale(2, RoundingMode.HALF_UP);
    }

    /** @return total a pagar (subtotal más IVA), con escala 2 */
    public BigDecimal getTotal() {
        return getSubtotal().add(getTaxAmount()).setScale(2, RoundingMode.HALF_UP);
    }

    /** @return número de factura */
    public String getInvoiceNumber() {
        return invoiceNumber;
    }

    /** @return reserva facturada */
    public Reservation getReservation() {
        return reservation;
    }

    /** @return líneas de la factura, no modificables */
    public List<InvoiceLine> getLines() {
        return Collections.unmodifiableList(lines);
    }

    /** @return tasa de IVA */
    public BigDecimal getTaxRate() {
        return taxRate;
    }

    /** @return fecha y hora de emisión */
    public LocalDateTime getIssuedAt() {
        return issuedAt;
    }

    private BigDecimal sumByCategory(InvoiceLineCategory category) {
        BigDecimal sum = BigDecimal.ZERO;
        for (InvoiceLine line : lines) {
            if (line.getCategory() == category) {
                sum = sum.add(line.getAmount());
            }
        }
        return sum.setScale(2, RoundingMode.HALF_UP);
    }
}
