package com.hotel.reservations.model;

import java.time.LocalDateTime;

/**
 * Llave digital emitida al huésped en el check-in para abrir la habitación asignada.
 */
public class DigitalKey {

    private final String code;
    private final String roomNumber;
    private final String reservationId;
    private final LocalDateTime issuedAt;
    private boolean active = true;

    /**
     * @param code código único de la llave
     * @param roomNumber número de la habitación que abre
     * @param reservationId reserva a la que pertenece
     * @param issuedAt momento de emisión
     */
    public DigitalKey(String code, String roomNumber, String reservationId, LocalDateTime issuedAt) {
        this.code = code;
        this.roomNumber = roomNumber;
        this.reservationId = reservationId;
        this.issuedAt = issuedAt;
    }

    /** Revoca la llave; a partir de ese momento no abre ninguna puerta. */
    public void revoke() {
        this.active = false;
    }

    /**
     * @param roomNumber número de habitación a verificar
     * @return {@code true} si la llave está activa y corresponde a esa habitación
     */
    public boolean opens(String roomNumber) {
        return active && this.roomNumber.equals(roomNumber);
    }

    /** @return código de la llave */
    public String getCode() {
        return code;
    }

    /** @return número de la habitación que abre */
    public String getRoomNumber() {
        return roomNumber;
    }

    /** @return identificador de la reserva asociada */
    public String getReservationId() {
        return reservationId;
    }

    /** @return momento de emisión */
    public LocalDateTime getIssuedAt() {
        return issuedAt;
    }

    /** @return {@code true} si la llave aún está activa */
    public boolean isActive() {
        return active;
    }
}
