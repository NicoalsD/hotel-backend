package com.hotel.reservations.repository;

import com.hotel.reservations.model.Room;
import com.hotel.reservations.model.RoomType;

import java.util.List;
import java.util.Optional;

/**
 * Acceso a las habitaciones del hotel.
 */
public interface RoomRepository {

    /** @return todas las habitaciones ordenadas por identificador */
    List<Room> findAll();

    /**
     * @param id identificador de la habitación
     * @return la habitación, o vacío si no existe
     */
    Optional<Room> findById(Long id);

    /**
     * @param type tipo de habitación
     * @return habitaciones de ese tipo ordenadas por identificador
     */
    List<Room> findByType(RoomType type);

    /**
     * Guarda o reemplaza una habitación.
     *
     * @param room habitación a guardar
     * @return la misma habitación guardada
     */
    Room save(Room room);

    /** @return cantidad de habitaciones almacenadas */
    long count();
}
