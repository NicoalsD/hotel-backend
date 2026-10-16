package com.hotel.reservations.repository.postgres;

import com.hotel.reservations.model.Invoice;
import com.hotel.reservations.model.InvoiceLine;
import com.hotel.reservations.model.InvoiceLineCategory;
import com.hotel.reservations.model.Reservation;
import com.hotel.reservations.repository.InvoiceRepository;
import com.hotel.reservations.repository.ReservationRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Repositorio de facturas con PostgreSQL (write-through). Las facturas viven en caché y cada
 * {@link #save(Invoice)} reemplaza la factura y sus líneas en la base de datos.
 */
@Repository
@Profile("postgres")
public class PostgresInvoiceRepository implements InvoiceRepository {

    private static final String UPSERT_INVOICE_SQL = """
            INSERT INTO invoices (invoice_number, reservation_id, issued_at, tax_rate)
            VALUES (?, ?, ?, ?)
            ON CONFLICT (invoice_number) DO UPDATE SET issued_at = EXCLUDED.issued_at,
                tax_rate = EXCLUDED.tax_rate
            """;

    private static final String INSERT_LINE_SQL = """
            INSERT INTO invoice_lines (invoice_number, line_no, description, category, quantity, unit_price)
            VALUES (?, ?, ?, ?, ?, ?)
            """;

    private final Map<String, Invoice> invoices = new ConcurrentHashMap<>();
    private final JdbcTemplate jdbcTemplate;
    private final ReservationRepository reservationRepository;

    /**
     * @param jdbcTemplate acceso JDBC a PostgreSQL
     * @param reservationRepository repositorio de reservas (debe estar cargado antes de {@link #loadAll()})
     */
    public PostgresInvoiceRepository(JdbcTemplate jdbcTemplate, ReservationRepository reservationRepository) {
        this.jdbcTemplate = jdbcTemplate;
        this.reservationRepository = reservationRepository;
    }

    /**
     * Carga todas las facturas con sus líneas en la caché. Se usa al arrancar.
     *
     * @return facturas cargadas, ordenadas por número
     */
    public List<Invoice> loadAll() {
        Map<String, List<InvoiceLine>> linesByInvoice = new ConcurrentHashMap<>();
        jdbcTemplate.query("SELECT * FROM invoice_lines ORDER BY invoice_number, line_no", rs -> {
            InvoiceLine line = new InvoiceLine(
                    rs.getString("description"),
                    InvoiceLineCategory.valueOf(rs.getString("category")),
                    rs.getLong("quantity"),
                    rs.getBigDecimal("unit_price"));
            linesByInvoice.computeIfAbsent(rs.getString("invoice_number"), k -> new ArrayList<>()).add(line);
        });
        jdbcTemplate.query("SELECT * FROM invoices", rs -> {
            String number = rs.getString("invoice_number");
            Reservation reservation = reservationRepository.findById(rs.getString("reservation_id"))
                    .orElseThrow(() -> new IllegalStateException("Reserva inexistente para la factura " + number));
            Invoice invoice = new Invoice(number, reservation,
                    linesByInvoice.getOrDefault(number, List.of()),
                    rs.getBigDecimal("tax_rate"),
                    rs.getObject("issued_at", LocalDateTime.class));
            invoices.put(number, invoice);
        });
        return findAll();
    }

    @Override
    public Invoice save(Invoice invoice) {
        String number = Objects.requireNonNull(invoice.getInvoiceNumber(), "La factura debe tener número");
        jdbcTemplate.update(UPSERT_INVOICE_SQL, number, invoice.getReservation().getId(),
                invoice.getIssuedAt(), invoice.getTaxRate());
        jdbcTemplate.update("DELETE FROM invoice_lines WHERE invoice_number = ?", number);
        List<InvoiceLine> lines = invoice.getLines();
        for (int i = 0; i < lines.size(); i++) {
            InvoiceLine line = lines.get(i);
            jdbcTemplate.update(INSERT_LINE_SQL, number, i + 1, line.getDescription(),
                    line.getCategory().name(), line.getQuantity(), line.getUnitPrice());
        }
        invoices.put(number, invoice);
        return invoice;
    }

    @Override
    public Optional<Invoice> findByReservationId(String reservationId) {
        if (reservationId == null) {
            return Optional.empty();
        }
        return invoices.values().stream()
                .filter(invoice -> reservationId.equals(invoice.getReservation().getId()))
                .findFirst();
    }

    @Override
    public Optional<Invoice> findByNumber(String number) {
        if (number == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(invoices.get(number));
    }

    @Override
    public List<Invoice> findAll() {
        return invoices.values().stream()
                .sorted(Comparator.comparing(Invoice::getInvoiceNumber))
                .collect(Collectors.toList());
    }
}
