package com.hotel.reservations.repository.postgres;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Columnas crudas de una fila de {@code reservations}, antes de reconstruir el dominio.
 *
 * @param id identificador de la reserva
 * @param roomId identificador de la habitación
 * @param guestFullName nombre del huésped
 * @param guestDocument documento del huésped
 * @param guestEmail correo del huésped
 * @param guestPhone teléfono del huésped
 * @param checkIn fecha de llegada
 * @param checkOut fecha de salida
 * @param roomTotal total del hospedaje
 * @param status estado de la reserva
 * @param createdAt momento de creación
 * @param checkedInAt momento del check-in, o {@code null}
 * @param checkedOutAt momento del check-out, o {@code null}
 * @param invoiceNumber número de factura, o {@code null}
 * @param keyCode código de la llave, o {@code null}
 * @param keyIssuedAt emisión de la llave, o {@code null}
 * @param keyActive estado de la llave, o {@code null}
 */
record ReservationRecord(String id, long roomId, String guestFullName, String guestDocument,
                         String guestEmail, String guestPhone, LocalDate checkIn, LocalDate checkOut,
                         BigDecimal roomTotal, String status, LocalDateTime createdAt,
                         LocalDateTime checkedInAt, LocalDateTime checkedOutAt, String invoiceNumber,
                         String keyCode, LocalDateTime keyIssuedAt, Boolean keyActive) {
}
