package com.hotel.reservations.model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;

/**
 * Servicio adicional contratado para una reserva. Es la clase base del patrón Factory;
 * cada subclase define cómo se calcula su cantidad a partir de las noches de la reserva.
 */
public abstract class AdditionalService {

    private final String id;
    private final AdditionalServiceType type;
    private final int quantity;
    private final LocalDateTime addedAt;

    /**
     * @param id identificador del servicio (por ejemplo "SRV-1")
     * @param type tipo de servicio del catálogo
     * @param reservationNights número de noches de la reserva a la que se agrega
     */
    protected AdditionalService(String id, AdditionalServiceType type, long reservationNights) {
        this.id = id;
        this.type = type;
        this.quantity = calculateQuantity(reservationNights);
        this.addedAt = LocalDateTime.now();
    }

    /**
     * Calcula la cantidad facturable del servicio.
     * Las subclases no deben depender de sus propios campos, porque se invoca desde el constructor.
     *
     * @param reservationNights número de noches de la reserva
     * @return cantidad a facturar
     */
    protected abstract int calculateQuantity(long reservationNights);

    /** @return identificador del servicio */
    public String getId() {
        return id;
    }

    /** @return tipo de servicio */
    public AdditionalServiceType getType() {
        return type;
    }

    /** @return cantidad facturable */
    public int getQuantity() {
        return quantity;
    }

    /** @return momento en que se agregó el servicio */
    public LocalDateTime getAddedAt() {
        return addedAt;
    }

    /** @return precio unitario según el catálogo */
    public BigDecimal getUnitPrice() {
        return type.getUnitPrice();
    }

    /** @return precio unitario multiplicado por la cantidad, con escala 2 */
    public BigDecimal getTotal() {
        return getUnitPrice().multiply(BigDecimal.valueOf(quantity)).setScale(2, RoundingMode.HALF_UP);
    }

    /** @return etiqueta en español del servicio */
    public String getLabel() {
        return type.getLabel();
    }
}
