package com.hotel.reservations.model.service;

import com.hotel.reservations.model.AdditionalService;
import com.hotel.reservations.model.AdditionalServiceType;

public class SpaService extends AdditionalService {

    public SpaService(String id, long reservationNights) {
        super(id, AdditionalServiceType.SPA, reservationNights);
    }

    @Override
    protected int calculateQuantity(long reservationNights) {
        return 1;
    }
}
