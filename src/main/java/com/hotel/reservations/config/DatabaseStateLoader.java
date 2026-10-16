package com.hotel.reservations.config;

import com.hotel.reservations.model.Reservation;
import com.hotel.reservations.model.ReservationStatus;
import com.hotel.reservations.model.Room;
import com.hotel.reservations.repository.postgres.PostgresInvoiceRepository;
import com.hotel.reservations.repository.postgres.PostgresReservationRepository;
import com.hotel.reservations.repository.postgres.PostgresRoomRepository;
import com.hotel.reservations.util.SequenceGenerator;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Carga al arrancar el estado persistido en PostgreSQL hacia las colecciones de trabajo:
 * habitaciones, reservas (con servicios y llaves) y facturas. Reconstruye las reservas activas
 * en cada habitación y avanza los generadores de identificadores para no repetirlos.
 * Se ejecuta antes que {@link RoomSeeder}, que solo siembra si no hay habitaciones.
 */
@Component
@Profile("postgres")
@Order(0)
public class DatabaseStateLoader implements ApplicationRunner {

    private final PostgresRoomRepository roomRepository;
    private final PostgresReservationRepository reservationRepository;
    private final PostgresInvoiceRepository invoiceRepository;
    private final SequenceGenerator sequenceGenerator;

    /**
     * @param roomRepository repositorio de habitaciones en PostgreSQL
     * @param reservationRepository repositorio de reservas en PostgreSQL
     * @param invoiceRepository repositorio de facturas en PostgreSQL
     * @param sequenceGenerator generador de identificadores a avanzar
     */
    public DatabaseStateLoader(PostgresRoomRepository roomRepository,
                               PostgresReservationRepository reservationRepository,
                               PostgresInvoiceRepository invoiceRepository,
                               SequenceGenerator sequenceGenerator) {
        this.roomRepository = roomRepository;
        this.reservationRepository = reservationRepository;
        this.invoiceRepository = invoiceRepository;
        this.sequenceGenerator = sequenceGenerator;
    }

    /**
     * @param args argumentos de la aplicación (no se usan)
     */
    @Override
    public void run(ApplicationArguments args) {
        roomRepository.loadAll();
        List<Reservation> reservations = reservationRepository.loadAll();
        for (Reservation reservation : reservations) {
            if (reservation.getStatus() == ReservationStatus.CONFIRMED
                    || reservation.getStatus() == ReservationStatus.CHECKED_IN) {
                Room room = reservation.getRoom();
                room.addBooking(reservation.getId(), reservation.getRange());
            }
        }
        invoiceRepository.loadAll();
        advanceSequences(reservations);
    }

    private void advanceSequences(List<Reservation> reservations) {
        long lastReservation = reservations.stream()
                .mapToLong(reservation -> parseNumber(reservation.getId(), "RES-"))
                .max().orElse(0L);
        long lastInvoice = invoiceRepository.findAll().stream()
                .mapToLong(invoice -> parseNumber(invoice.getInvoiceNumber(), "FAC-"))
                .max().orElse(0L);
        long lastService = reservations.stream()
                .flatMap(reservation -> reservation.getServices().stream())
                .mapToLong(service -> parseNumber(service.getId(), "SRV-"))
                .max().orElse(0L);
        sequenceGenerator.advanceTo(lastReservation, lastInvoice, lastService);
    }

    private static long parseNumber(String identifier, String prefix) {
        return Long.parseLong(identifier.substring(prefix.length()));
    }
}
