package com.hotel.reservations.model.service;

import com.hotel.reservations.model.AdditionalService;
import com.hotel.reservations.model.AdditionalServiceType;

/**
 * Servicio de desayuno. Se cobra por cada noche de la reserva.
 */
public class BreakfastService extends AdditionalService {

    /**
     * @param id identificador del servicio
     * @param reservationNights noches de la reserva; determinan la cantidad facturada
     */
    public BreakfastService(String id, long reservationNights) {
        super(id, AdditionalServiceType.BREAKFAST, reservationNights);
    }

    /** @return número de noches de la reserva */
    @Override
    protected int calculateQuantity(long reservationNights) {
        return Math.toIntExact(reservationNights);
    }
}
