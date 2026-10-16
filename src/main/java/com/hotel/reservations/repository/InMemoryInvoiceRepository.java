package com.hotel.reservations.repository;

import com.hotel.reservations.model.Invoice;
import org.springframework.stereotype.Repository;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Repositorio de facturas en memoria, indexado por número de factura.
 */
@Repository
public class InMemoryInvoiceRepository implements InvoiceRepository {

    private final Map<String, Invoice> invoices = new ConcurrentHashMap<>();

    @Override
    public Invoice save(Invoice invoice) {
        invoices.put(Objects.requireNonNull(invoice.getInvoiceNumber(), "La factura debe tener número"), invoice);
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
