package com.hotel.reservations.factory;

import com.hotel.reservations.model.AdditionalService;
import com.hotel.reservations.model.AdditionalServiceType;
import com.hotel.reservations.model.service.BreakfastService;
import com.hotel.reservations.model.service.SpaService;
import com.hotel.reservations.model.service.TransferService;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicLong;

@Component
public class AdditionalServiceFactory {

    private final AtomicLong sequence = new AtomicLong();

    public AdditionalService create(AdditionalServiceType type, long reservationNights) {
        String id = "SRV-" + sequence.incrementAndGet();
        return switch (type) {
            case SPA -> new SpaService(id, reservationNights);
            case BREAKFAST -> new BreakfastService(id, reservationNights);
            case TRANSFER -> new TransferService(id, reservationNights);
        };
    }
}
