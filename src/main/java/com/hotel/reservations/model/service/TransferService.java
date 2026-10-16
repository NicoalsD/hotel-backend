package com.hotel.reservations.model.service;

import com.hotel.reservations.model.AdditionalService;
import com.hotel.reservations.model.AdditionalServiceType;

public class TransferService extends AdditionalService {

    public TransferService(String id, long reservationNights) {
        super(id, AdditionalServiceType.TRANSFER, reservationNights);
    }

    @Override
    protected int calculateQuantity(long reservationNights) {
        return 1;
    }
}
