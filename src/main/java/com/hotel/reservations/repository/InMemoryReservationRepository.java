package com.hotel.reservations.repository;

import com.hotel.reservations.model.Reservation;
import org.springframework.stereotype.Repository;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Repositorio de reservas en memoria, respaldado por un {@link ConcurrentHashMap}.
 * Las consultas devuelven las reservas de la más reciente a la más antigua; si dos reservas
 * tienen la misma fecha de creación, se desempata por identificador descendente.
 */
@Repository
public class InMemoryReservationRepository implements ReservationRepository {

    private static final Comparator<Reservation> NEWEST_FIRST = Comparator
            .comparing(Reservation::getCreatedAt)
            .thenComparing(Reservation::getId)
            .reversed();

    private final Map<String, Reservation> reservations = new ConcurrentHashMap<>();

    @Override
    public Reservation save(Reservation reservation) {
        reservations.put(Objects.requireNonNull(reservation.getId(), "La reserva debe tener identificador"),
                reservation);
        return reservation;
    }

    @Override
    public Optional<Reservation> findById(String id) {
        if (id == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(reservations.get(id));
    }

    @Override
    public List<Reservation> findAll() {
        return reservations.values().stream()
                .sorted(NEWEST_FIRST)
                .collect(Collectors.toList());
    }

    @Override
    public List<Reservation> findByRoomId(Long roomId) {
        if (roomId == null) {
            return List.of();
        }
        return reservations.values().stream()
                .filter(reservation -> roomId.equals(reservation.getRoom().getId()))
                .sorted(NEWEST_FIRST)
                .collect(Collectors.toList());
    }
}
