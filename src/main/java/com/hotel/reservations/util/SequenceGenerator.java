package com.hotel.reservations.util;

import org.springframework.stereotype.Component;

import java.util.Locale;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Genera identificadores secuenciales y thread-safe para reservas, facturas y servicios.
 */
@Component
public class SequenceGenerator {

    private static final long FIRST_RESERVATION_NUMBER = 1001L;
    private static final long FIRST_NUMBER = 1L;

    private final AtomicLong reservationSequence = new AtomicLong(FIRST_RESERVATION_NUMBER);
    private final AtomicLong invoiceSequence = new AtomicLong(FIRST_NUMBER);
    private final AtomicLong serviceSequence = new AtomicLong(FIRST_NUMBER);

    /** @return siguiente identificador de reserva, por ejemplo "RES-1001" */
    public String nextReservationId() {
        return "RES-" + reservationSequence.getAndIncrement();
    }

    /** @return siguiente número de factura, por ejemplo "FAC-0001" */
    public String nextInvoiceNumber() {
        return String.format(Locale.ROOT, "FAC-%04d", invoiceSequence.getAndIncrement());
    }

    /** @return siguiente identificador de servicio adicional, por ejemplo "SRV-1" */
    public String nextServiceId() {
        return "SRV-" + serviceSequence.getAndIncrement();
    }
}
