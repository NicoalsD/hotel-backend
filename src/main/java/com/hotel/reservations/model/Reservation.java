package com.hotel.reservations.model;

import com.hotel.reservations.exception.InvalidReservationStateException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Reserva de una habitación. Controla las transiciones de estado
 * (CONFIRMED -> CHECKED_IN -> CHECKED_OUT) y los servicios adicionales contratados.
 */
public class Reservation {

    private final String id;
    private final Guest guest;
    private final Room room;
    private final DateRange range;
    private final BigDecimal roomTotal;
    private final LocalDateTime createdAt;
    private final List<AdditionalService> services = new ArrayList<>();
    private ReservationStatus status = ReservationStatus.CONFIRMED;
    private DigitalKey digitalKey;
    private String invoiceNumber;
    private LocalDateTime checkedInAt;
    private LocalDateTime checkedOutAt;

    /**
     * @param id identificador de la reserva (por ejemplo "RES-1001")
     * @param guest huésped titular
     * @param room habitación reservada
     * @param range fechas de la estancia
     * @param roomTotal total del hospedaje calculado al reservar
     */
    public Reservation(String id, Guest guest, Room room, DateRange range, BigDecimal roomTotal) {
        this.id = id;
        this.guest = guest;
        this.room = room;
        this.range = range;
        this.roomTotal = roomTotal;
        this.createdAt = LocalDateTime.now();
    }

    /**
     * Realiza el check-in: asocia la llave digital y pasa la reserva a CHECKED_IN.
     *
     * @param key llave digital emitida para la estancia
     * @throws InvalidReservationStateException si la reserva no está CONFIRMED
     */
    public void checkIn(DigitalKey key) {
        if (status != ReservationStatus.CONFIRMED) {
            throw new InvalidReservationStateException("La reserva " + id
                    + " no puede hacer check-in porque su estado es " + status.getLabel());
        }
        this.digitalKey = key;
        this.status = ReservationStatus.CHECKED_IN;
        this.checkedInAt = LocalDateTime.now();
    }

    /**
     * Realiza el check-out y pasa la reserva a CHECKED_OUT.
     *
     * @param invoiceNumber número de la factura emitida
     * @throws InvalidReservationStateException si la reserva no está CHECKED_IN
     */
    public void checkOut(String invoiceNumber) {
        if (status != ReservationStatus.CHECKED_IN) {
            throw new InvalidReservationStateException("La reserva " + id
                    + " no puede hacer check-out porque su estado es " + status.getLabel());
        }
        this.invoiceNumber = invoiceNumber;
        this.status = ReservationStatus.CHECKED_OUT;
        this.checkedOutAt = LocalDateTime.now();
    }

    /** @return {@code true} si la reserva admite servicios (CONFIRMED o CHECKED_IN) */
    public boolean canAddServices() {
        return status == ReservationStatus.CONFIRMED || status == ReservationStatus.CHECKED_IN;
    }

    /**
     * Agrega un servicio adicional a la reserva.
     *
     * @param service servicio ya creado
     * @throws InvalidReservationStateException si la reserva no admite servicios en su estado actual
     */
    public void addService(AdditionalService service) {
        if (!canAddServices()) {
            throw new InvalidReservationStateException("La reserva " + id
                    + " no permite agregar servicios porque su estado es " + status.getLabel());
        }
        services.add(service);
    }

    /** @return {@code true} si la reserva está CONFIRMED o CHECKED_IN */
    public boolean isActive() {
        return status == ReservationStatus.CONFIRMED || status == ReservationStatus.CHECKED_IN;
    }

    /** @return número de noches de la estancia */
    public long getNights() {
        return range.getNights();
    }

    /** @return suma de los servicios agregados, con escala 2 */
    public BigDecimal getServicesTotal() {
        BigDecimal total = BigDecimal.ZERO;
        for (AdditionalService service : services) {
            total = total.add(service.getTotal());
        }
        return total.setScale(2, RoundingMode.HALF_UP);
    }

    /** @return hospedaje más servicios, con escala 2 */
    public BigDecimal getEstimatedTotal() {
        return roomTotal.add(getServicesTotal()).setScale(2, RoundingMode.HALF_UP);
    }

    /** @return identificador de la reserva */
    public String getId() {
        return id;
    }

    /** @return huésped titular */
    public Guest getGuest() {
        return guest;
    }

    /** @return habitación reservada */
    public Room getRoom() {
        return room;
    }

    /** @return fechas de la estancia */
    public DateRange getRange() {
        return range;
    }

    /** @return total del hospedaje */
    public BigDecimal getRoomTotal() {
        return roomTotal;
    }

    /** @return estado actual */
    public ReservationStatus getStatus() {
        return status;
    }

    /** @return llave digital asociada, o {@code null} si aún no hay check-in */
    public DigitalKey getDigitalKey() {
        return digitalKey;
    }

    /** @return número de factura, o {@code null} si aún no hay check-out */
    public String getInvoiceNumber() {
        return invoiceNumber;
    }

    /** @return momento de creación */
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    /** @return momento del check-in, o {@code null} */
    public LocalDateTime getCheckedInAt() {
        return checkedInAt;
    }

    /** @return momento del check-out, o {@code null} */
    public LocalDateTime getCheckedOutAt() {
        return checkedOutAt;
    }

    /** @return servicios agregados, no modificables */
    public List<AdditionalService> getServices() {
        return Collections.unmodifiableList(services);
    }
}
