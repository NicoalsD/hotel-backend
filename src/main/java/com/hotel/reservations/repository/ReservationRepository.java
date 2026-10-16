package com.hotel.reservations.repository;

import com.hotel.reservations.model.Reservation;

import java.util.List;
import java.util.Optional;

/**
 * Acceso a las reservas del hotel.
 */
public interface ReservationRepository {

    /**
     * Guarda o reemplaza una reserva.
     *
     * @param reservation reserva a guardar
     * @return la misma reserva guardada
     */
    Reservation save(Reservation reservation);

    /**
     * @param id identificador de la reserva (por ejemplo "RES-1001")
     * @return la reserva, o vacío si no existe
     */
    Optional<Reservation> findById(String id);

    /** @return todas las reservas, de la más reciente a la más antigua */
    List<Reservation> findAll();

    /**
     * @param roomId identificador de la habitación
     * @return reservas de esa habitación, de la más reciente a la más antigua
     */
    List<Reservation> findByRoomId(Long roomId);
}
