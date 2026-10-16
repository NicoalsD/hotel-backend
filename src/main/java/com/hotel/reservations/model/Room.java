package com.hotel.reservations.model;

import com.hotel.reservations.exception.RoomNotAvailableException;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Habitación del hotel. Controla sus propias reservas (reservationId -> rango de fechas)
 * y su estado físico (disponible u ocupada).
 */
public class Room {

    private final Long id;
    private final String number;
    private final RoomType type;
    private final int floor;
    private final String description;
    private RoomStatus status = RoomStatus.AVAILABLE;
    private final Map<String, DateRange> bookings = new LinkedHashMap<>();

    /**
     * @param id identificador de la habitación
     * @param number número visible de la habitación (por ejemplo "201")
     * @param type tipo de habitación
     * @param floor piso donde se encuentra
     * @param description descripción en español
     */
    public Room(Long id, String number, RoomType type, int floor, String description) {
        this.id = id;
        this.number = number;
        this.type = type;
        this.floor = floor;
        this.description = description;
    }

    /** @return identificador */
    public Long getId() {
        return id;
    }

    /** @return número de la habitación */
    public String getNumber() {
        return number;
    }

    /** @return tipo de habitación */
    public RoomType getType() {
        return type;
    }

    /** @return piso */
    public int getFloor() {
        return floor;
    }

    /** @return descripción */
    public String getDescription() {
        return description;
    }

    /** @return estado físico actual */
    public RoomStatus getStatus() {
        return status;
    }

    /** @return tarifa base por noche según el tipo */
    public BigDecimal getBasePrice() {
        return type.getBasePrice();
    }

    /** @return capacidad máxima según el tipo */
    public int getCapacity() {
        return type.getCapacity();
    }

    /**
     * Indica si la habitación no tiene reservas que se solapen con el rango dado.
     *
     * @param range rango de fechas a consultar
     * @return {@code true} si puede reservarse en ese rango
     */
    public boolean isAvailableFor(DateRange range) {
        for (DateRange booked : bookings.values()) {
            if (booked.overlaps(range)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Registra una reserva sobre la habitación.
     *
     * @param reservationId identificador de la reserva
     * @param range rango de fechas reservado
     * @throws RoomNotAvailableException si el rango se solapa con otra reserva
     */
    public void addBooking(String reservationId, DateRange range) {
        if (!isAvailableFor(range)) {
            throw new RoomNotAvailableException(
                    "La habitación " + number + " no está disponible en las fechas seleccionadas");
        }
        bookings.put(reservationId, range);
    }

    /**
     * Elimina la reserva indicada de la habitación, si existe.
     *
     * @param reservationId identificador de la reserva
     */
    public void removeBooking(String reservationId) {
        bookings.remove(reservationId);
    }

    /**
     * Restaura el estado físico persistido de la habitación, sin efectos secundarios.
     *
     * @param status estado persistido
     */
    public void restoreStatus(RoomStatus status) {
        this.status = status;
    }

    /** Marca la habitación como ocupada (check-in realizado). */
    public void markOccupied() {
        this.status = RoomStatus.OCCUPIED;
    }

    /** Marca la habitación como disponible (check-out realizado). */
    public void markAvailable() {
        this.status = RoomStatus.AVAILABLE;
    }

    /** @return copia no modificable de las reservas actuales */
    public Map<String, DateRange> getBookings() {
        return Collections.unmodifiableMap(new LinkedHashMap<>(bookings));
    }
}
