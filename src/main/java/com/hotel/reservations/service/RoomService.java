package com.hotel.reservations.service;

import com.hotel.reservations.exception.ResourceNotFoundException;
import com.hotel.reservations.model.DateRange;
import com.hotel.reservations.model.Room;
import com.hotel.reservations.model.RoomType;
import com.hotel.reservations.repository.ReservationRepository;
import com.hotel.reservations.repository.RoomRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

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

    /**
     * Indica si una habitación puede reservarse en el rango dado.
     *
     * @param roomId identificador de la habitación
     * @param range rango de fechas solicitado
     * @return {@code true} si no hay reservas que se solapen con el rango
     * @throws ResourceNotFoundException si la habitación no existe
     */
    public synchronized boolean isRoomAvailable(Long roomId, DateRange range) {
        return findById(roomId).isAvailableFor(range);
    }

    /**
     * Devuelve las habitaciones disponibles en el rango dado, opcionalmente filtradas por tipo.
     *
     * @param range rango de fechas solicitado
     * @param typeOrNull tipo de habitación, o {@code null} para todos
     * @return habitaciones libres ordenadas por identificador
     */
    public synchronized List<Room> findAvailableRooms(DateRange range, RoomType typeOrNull) {
        return findByType(typeOrNull).stream()
                .filter(room -> room.isAvailableFor(range))
                .collect(Collectors.toList());
    }

    /**
     * Registra una reserva sobre una habitación. El modelo valida el solapamiento.
     *
     * @param roomId identificador de la habitación
     * @param reservationId identificador de la reserva
     * @param range rango de fechas reservado
     * @throws ResourceNotFoundException si la habitación no existe
     * @throws com.hotel.reservations.exception.RoomNotAvailableException si la habitación ya está reservada
     */
    public synchronized void reserveRoom(Long roomId, String reservationId, DateRange range) {
        findById(roomId).addBooking(reservationId, range);
    }

    /**
     * Libera una reserva de la habitación y la marca como disponible.
     *
     * @param roomId identificador de la habitación
     * @param reservationId identificador de la reserva a liberar
     * @throws ResourceNotFoundException si la habitación no existe
     */
    public synchronized void releaseRoom(Long roomId, String reservationId) {
        Room room = findById(roomId);
        room.removeBooking(reservationId);
        room.markAvailable();
    }

    /**
     * Marca la habitación como ocupada al realizar el check-in.
     *
     * @param roomId identificador de la habitación
     * @throws ResourceNotFoundException si la habitación no existe
     */
    public synchronized void occupyRoom(Long roomId) {
        findById(roomId).markOccupied();
    }
}
