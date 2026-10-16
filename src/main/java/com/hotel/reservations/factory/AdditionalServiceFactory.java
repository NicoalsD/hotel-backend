package com.hotel.reservations.factory;

import com.hotel.reservations.model.AdditionalService;
import com.hotel.reservations.model.AdditionalServiceType;
import com.hotel.reservations.model.service.BreakfastService;
import com.hotel.reservations.model.service.SpaService;
import com.hotel.reservations.model.service.TransferService;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Fábrica de servicios adicionales (patrón Factory). Crea la subclase que corresponde
 * al tipo solicitado y asigna un identificador secuencial ("SRV-1", "SRV-2", ...).
 * El contador es de instancia, por lo que cada fábrica numera sus propios servicios.
 */
@Component
public class AdditionalServiceFactory {

    private final AtomicLong sequence = new AtomicLong();

    /**
     * @param type tipo de servicio del catálogo; no debe ser nulo
     * @param reservationNights noches de la reserva a la que se agrega el servicio
     * @return nueva instancia del servicio con su identificador
     * @throws NullPointerException si el tipo es nulo
     */
    public AdditionalService create(AdditionalServiceType type, long reservationNights) {
        Objects.requireNonNull(type, "El tipo de servicio es obligatorio");
        String id = "SRV-" + sequence.incrementAndGet();
        return switch (type) {
            case SPA -> new SpaService(id, reservationNights);
            case BREAKFAST -> new BreakfastService(id, reservationNights);
            case TRANSFER -> new TransferService(id, reservationNights);
        };
    }
}
