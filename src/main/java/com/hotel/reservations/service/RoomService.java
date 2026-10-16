package com.hotel.reservations.service;

import com.hotel.reservations.exception.ResourceNotFoundException;
import com.hotel.reservations.model.Room;
import com.hotel.reservations.model.RoomType;
import com.hotel.reservations.repository.ReservationRepository;
import com.hotel.reservations.repository.RoomRepository;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Subsistema de habitaciones (HabitacionService): consulta, reserva y liberación de habitaciones.
 * Todas las operaciones que leen o modifican las reservas internas de una habitación están
 * sincronizadas, porque {@link Room} no es thread-safe.
 */
@Service
public class RoomService {

    private final RoomRepository roomRepository;
    private final ReservationRepository reservationRepository;

    /**
     * @param roomRepository repositorio de habitaciones
     * @param reservationRepository repositorio de reservas
     */
    public RoomService(RoomRepository roomRepository, ReservationRepository reservationRepository) {
        this.roomRepository = roomRepository;
        this.reservationRepository = reservationRepository;
    }

    /** @return todas las habitaciones ordenadas por identificador */
    public List<Room> findAll() {
        return roomRepository.findAll();
    }

    /**
     * @param id identificador de la habitación
     * @return la habitación
     * @throws ResourceNotFoundException si no existe
     */
    public Room findById(Long id) {
        return roomRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró la habitación " + id));
    }

    /**
     * @param typeOrNull tipo de habitación, o {@code null} para todas
     * @return habitaciones del tipo indicado (o todas) ordenadas por identificador
     */
    public List<Room> findByType(RoomType typeOrNull) {
        if (typeOrNull == null) {
            return roomRepository.findAll();
        }
        return roomRepository.findByType(typeOrNull);
    }
}
