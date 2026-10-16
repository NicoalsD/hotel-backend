package com.hotel.reservations.model.service;

import com.hotel.reservations.model.AdditionalService;
import com.hotel.reservations.model.AdditionalServiceType;

/**
 * Servicio de spa. Es un cobro único, por lo que su cantidad es siempre 1.
 */
public class SpaService extends AdditionalService {

    /**
     * @param id identificador del servicio
     * @param reservationNights noches de la reserva (no afectan la cantidad)
     */
    public SpaService(String id, long reservationNights) {
        super(id, AdditionalServiceType.SPA, reservationNights);
    }

    /** @return siempre 1 */
    @Override
    protected int calculateQuantity(long reservationNights) {
        return 1;
    }
}
