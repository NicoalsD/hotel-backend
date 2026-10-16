package com.hotel.reservations.repository;

import com.hotel.reservations.model.Invoice;

import java.util.List;
import java.util.Optional;

/**
 * Acceso a las facturas emitidas en el check-out.
 */
public interface InvoiceRepository {

    /**
     * Guarda o reemplaza una factura.
     *
     * @param invoice factura a guardar
     * @return la misma factura guardada
     */
    Invoice save(Invoice invoice);

    /**
     * @param reservationId identificador de la reserva facturada
     * @return la factura de la reserva, o vacío si aún no existe
     */
    Optional<Invoice> findByReservationId(String reservationId);

    /**
     * @param number número de factura (por ejemplo "FAC-0001")
     * @return la factura, o vacío si no existe
     */
    Optional<Invoice> findByNumber(String number);

    /** @return todas las facturas ordenadas por número */
    List<Invoice> findAll();
}
