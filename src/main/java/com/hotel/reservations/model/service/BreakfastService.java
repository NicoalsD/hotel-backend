package com.hotel.reservations.model.service;

import com.hotel.reservations.model.AdditionalService;
import com.hotel.reservations.model.AdditionalServiceType;

public class BreakfastService extends AdditionalService {

    public BreakfastService(String id, long reservationNights) {
        super(id, AdditionalServiceType.BREAKFAST, reservationNights);
    }

    @Override
    protected int calculateQuantity(long reservationNights) {
        return Math.toIntExact(reservationNights);
    }
}
