package com.hotel.reservations.factory;

import com.hotel.reservations.model.AdditionalService;
import com.hotel.reservations.model.AdditionalServiceType;
import com.hotel.reservations.model.service.BreakfastService;
import com.hotel.reservations.model.service.SpaService;
import com.hotel.reservations.model.service.TransferService;
import org.springframework.stereotype.Component;

import com.hotel.reservations.util.SequenceGenerator;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Fábrica de servicios adicionales (patrón Factory). Crea la subclase que corresponde
 * al tipo solicitado y asigna un identificador secuencial ("SRV-1", "SRV-2", ...) tomado
 * del {@link SequenceGenerator}, de modo que los identificadores no se repiten entre reinicios.
 */
@Component
public class AdditionalServiceFactory {

    private final SequenceGenerator sequenceGenerator;

    /**
     * @param sequenceGenerator generador compartido de identificadores de servicio
     */
    public AdditionalServiceFactory(SequenceGenerator sequenceGenerator) {
        this.sequenceGenerator = sequenceGenerator;
    }

    /**
     * @param type tipo de servicio del catálogo; no debe ser nulo
     * @param reservationNights noches de la reserva a la que se agrega el servicio
     * @return nueva instancia del servicio con su identificador
     * @throws NullPointerException si el tipo es nulo
     */
    public AdditionalService create(AdditionalServiceType type, long reservationNights) {
        Objects.requireNonNull(type, "El tipo de servicio es obligatorio");
        return newInstance(sequenceGenerator.nextServiceId(), type, reservationNights);
    }

    /**
     * Reconstruye un servicio persistido con su identificador, cantidad y momento de adición originales.
     *
     * @param id identificador persistido del servicio
     * @param type tipo de servicio del catálogo; no debe ser nulo
     * @param quantity cantidad facturable persistida
     * @param addedAt momento persistido de la adición
     * @return servicio restaurado
     * @throws IllegalArgumentException si la cantidad persistida no coincide con la regla del tipo
     */
    public AdditionalService restore(String id, AdditionalServiceType type, int quantity, LocalDateTime addedAt) {
        Objects.requireNonNull(type, "El tipo de servicio es obligatorio");
        AdditionalService service = newInstance(id, type, quantity);
        if (service.getQuantity() != quantity) {
            throw new IllegalArgumentException("La cantidad " + quantity + " no es válida para el servicio " + id);
        }
        service.restoreAddedAt(addedAt);
        return service;
    }

    private static AdditionalService newInstance(String id, AdditionalServiceType type, long reservationNights) {
        return switch (type) {
            case SPA -> new SpaService(id, reservationNights);
            case BREAKFAST -> new BreakfastService(id, reservationNights);
            case TRANSFER -> new TransferService(id, reservationNights);
        };
    }
}
