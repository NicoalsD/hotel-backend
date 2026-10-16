package com.hotel.reservations.repository.postgres;

import com.hotel.reservations.factory.AdditionalServiceFactory;
import com.hotel.reservations.model.AdditionalService;
import com.hotel.reservations.model.AdditionalServiceType;
import com.hotel.reservations.model.DateRange;
import com.hotel.reservations.model.DigitalKey;
import com.hotel.reservations.model.Guest;
import com.hotel.reservations.model.Reservation;
import com.hotel.reservations.model.ReservationStatus;
import com.hotel.reservations.model.Room;
import com.hotel.reservations.repository.ReservationRepository;
import com.hotel.reservations.repository.RoomRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Repositorio de reservas con PostgreSQL (write-through). Las reservas viven en caché y cada
 * {@link #save(Reservation)} escribe la reserva y sus servicios en la base de datos.
 */
@Repository
@Profile("postgres")
public class PostgresReservationRepository implements ReservationRepository {

    private static final Comparator<Reservation> NEWEST_FIRST = Comparator
            .comparing(Reservation::getCreatedAt)
            .thenComparing(Reservation::getId)
            .reversed();

    private static final String UPSERT_RESERVATION_SQL = """
            INSERT INTO reservations (id, room_id, guest_full_name, guest_document, guest_email, guest_phone,
                check_in, check_out, room_total, status, created_at, checked_in_at, checked_out_at,
                invoice_number, key_code, key_issued_at, key_active)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            ON CONFLICT (id) DO UPDATE SET status = EXCLUDED.status, checked_in_at = EXCLUDED.checked_in_at,
                checked_out_at = EXCLUDED.checked_out_at, invoice_number = EXCLUDED.invoice_number,
                key_code = EXCLUDED.key_code, key_issued_at = EXCLUDED.key_issued_at,
                key_active = EXCLUDED.key_active
            """;

    private static final String UPSERT_SERVICE_SQL = """
            INSERT INTO reservation_services (id, reservation_id, type, quantity, added_at)
            VALUES (?, ?, ?, ?, ?)
            ON CONFLICT (id) DO NOTHING
            """;

    private final Map<String, Reservation> reservations = new ConcurrentHashMap<>();
    private final JdbcTemplate jdbcTemplate;
    private final RoomRepository roomRepository;
    private final AdditionalServiceFactory serviceFactory;

    /**
     * @param jdbcTemplate acceso JDBC a PostgreSQL
     * @param roomRepository repositorio de habitaciones (debe estar cargado antes de {@link #loadAll()})
     * @param serviceFactory fábrica usada para reconstruir los servicios persistidos
     */
    public PostgresReservationRepository(JdbcTemplate jdbcTemplate, RoomRepository roomRepository,
                                         AdditionalServiceFactory serviceFactory) {
        this.jdbcTemplate = jdbcTemplate;
        this.roomRepository = roomRepository;
        this.serviceFactory = serviceFactory;
    }

    /**
     * Carga todas las reservas con sus servicios y llaves en la caché. Se usa al arrancar.
     *
     * @return reservas cargadas, de la más reciente a la más antigua
     */
    public List<Reservation> loadAll() {
        Map<String, List<AdditionalService>> servicesByReservation = loadServices();
        List<ReservationRecord> records = jdbcTemplate.query("SELECT * FROM reservations",
                (rs, rowNum) -> new ReservationRecord(
                        rs.getString("id"), rs.getLong("room_id"), rs.getString("guest_full_name"),
                        rs.getString("guest_document"), rs.getString("guest_email"), rs.getString("guest_phone"),
                        rs.getObject("check_in", java.time.LocalDate.class),
                        rs.getObject("check_out", java.time.LocalDate.class),
                        rs.getBigDecimal("room_total"), rs.getString("status"),
                        rs.getObject("created_at", LocalDateTime.class),
                        rs.getObject("checked_in_at", LocalDateTime.class),
                        rs.getObject("checked_out_at", LocalDateTime.class),
                        rs.getString("invoice_number"), rs.getString("key_code"),
                        rs.getObject("key_issued_at", LocalDateTime.class),
                        (Boolean) rs.getObject("key_active")));
        for (ReservationRecord record : records) {
            Reservation reservation = toDomain(record,
                    servicesByReservation.getOrDefault(record.id(), List.of()));
            reservations.put(reservation.getId(), reservation);
        }
        return findAll();
    }

    private Map<String, List<AdditionalService>> loadServices() {
        Map<String, List<AdditionalService>> byReservation = new HashMap<>();
        jdbcTemplate.query("SELECT * FROM reservation_services ORDER BY added_at, id", rs -> {
            AdditionalService service = serviceFactory.restore(
                    rs.getString("id"),
                    AdditionalServiceType.valueOf(rs.getString("type")),
                    rs.getInt("quantity"),
                    rs.getObject("added_at", LocalDateTime.class));
            byReservation.computeIfAbsent(rs.getString("reservation_id"), k -> new ArrayList<>()).add(service);
        });
        return byReservation;
    }

    private Reservation toDomain(ReservationRecord record, List<AdditionalService> services) {
        Room room = roomRepository.findById(record.roomId())
                .orElseThrow(() -> new IllegalStateException("Habitación inexistente para la reserva " + record.id()));
        Guest guest = new Guest(record.guestFullName(), record.guestDocument(), record.guestEmail(),
                record.guestPhone());
        DigitalKey key = record.keyCode() == null ? null : DigitalKey.restore(record.keyCode(), room.getNumber(),
                record.id(), record.keyIssuedAt(), Boolean.TRUE.equals(record.keyActive()));
        return Reservation.restore(record.id(), guest, room,
                new DateRange(record.checkIn(), record.checkOut()), record.roomTotal(), record.createdAt(),
                ReservationStatus.valueOf(record.status()), services, key, record.invoiceNumber(),
                record.checkedInAt(), record.checkedOutAt());
    }

    @Override
    public Reservation save(Reservation reservation) {
        Objects.requireNonNull(reservation.getId(), "La reserva debe tener identificador");
        DigitalKey key = reservation.getDigitalKey();
        Guest guest = reservation.getGuest();
        jdbcTemplate.update(UPSERT_RESERVATION_SQL,
                reservation.getId(), reservation.getRoom().getId(), guest.getFullName(), guest.getDocumentNumber(),
                guest.getEmail(), guest.getPhone(), reservation.getRange().getCheckIn(),
                reservation.getRange().getCheckOut(), reservation.getRoomTotal(), reservation.getStatus().name(),
                reservation.getCreatedAt(), reservation.getCheckedInAt(), reservation.getCheckedOutAt(),
                reservation.getInvoiceNumber(), key == null ? null : key.getCode(),
                key == null ? null : key.getIssuedAt(), key == null ? null : key.isActive());
        for (AdditionalService service : reservation.getServices()) {
            jdbcTemplate.update(UPSERT_SERVICE_SQL, service.getId(), reservation.getId(),
                    service.getType().name(), service.getQuantity(), service.getAddedAt());
        }
        reservations.put(reservation.getId(), reservation);
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
