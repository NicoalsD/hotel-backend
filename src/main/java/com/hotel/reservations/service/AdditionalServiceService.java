package com.hotel.reservations.service;

import com.hotel.reservations.exception.InvalidReservationStateException;
import com.hotel.reservations.factory.AdditionalServiceFactory;
import com.hotel.reservations.model.AdditionalService;
import com.hotel.reservations.model.AdditionalServiceType;
import com.hotel.reservations.model.Reservation;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Objects;

/**
 * Servicio de servicios adicionales (ServicioAdicionalService). Valida el estado de la reserva
 * antes de crear el servicio, para no consumir un identificador cuando la operación se rechaza.
 */
@Service
public class AdditionalServiceService {

    private final AdditionalServiceFactory factory;

    /**
     * @param factory fábrica que crea las instancias de servicio
     */
    public AdditionalServiceService(AdditionalServiceFactory factory) {
        this.factory = factory;
    }

    /**
     * Crea un servicio con las noches de la reserva y lo agrega a ella.
     *
     * @param reservation reserva a la que se agrega el servicio
     * @param type tipo de servicio del catálogo
     * @return servicio agregado
     * @throws InvalidReservationStateException si la reserva no admite servicios en su estado actual
     */
    public AdditionalService addService(Reservation reservation, AdditionalServiceType type) {
        Objects.requireNonNull(reservation, "La reserva es obligatoria");
        Objects.requireNonNull(type, "El tipo de servicio es obligatorio");
        if (!reservation.canAddServices()) {
            throw new InvalidReservationStateException("La reserva " + reservation.getId()
                    + " no permite agregar servicios porque su estado es " + reservation.getStatus().getLabel());
        }
        AdditionalService service = factory.create(type, reservation.getNights());
        reservation.addService(service);
        return service;
    }

    /** @return todos los tipos de servicio del catálogo */
    public List<AdditionalServiceType> getCatalog() {
        return List.of(AdditionalServiceType.values());
    }

    /**
     * @param reservation reserva a consultar
     * @return suma de los totales de los servicios de la reserva, con escala 2
     */
    public BigDecimal calculateServicesTotal(Reservation reservation) {
        Objects.requireNonNull(reservation, "La reserva es obligatoria");
        BigDecimal total = BigDecimal.ZERO;
        for (AdditionalService service : reservation.getServices()) {
            total = total.add(service.getTotal());
        }
        return total.setScale(2, RoundingMode.HALF_UP);
    }
}
