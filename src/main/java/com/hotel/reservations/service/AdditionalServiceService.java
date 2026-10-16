package com.hotel.reservations.service;

import com.hotel.reservations.factory.AdditionalServiceFactory;
import com.hotel.reservations.model.AdditionalService;
import com.hotel.reservations.model.AdditionalServiceType;
import com.hotel.reservations.model.Reservation;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
public class AdditionalServiceService {

    private final AdditionalServiceFactory factory;

    public AdditionalServiceService(AdditionalServiceFactory factory) {
        this.factory = factory;
    }

    public AdditionalService addService(Reservation reservation, AdditionalServiceType type) {
        AdditionalService service = factory.create(type, reservation.getNights());
        reservation.addService(service);
        return service;
    }

    public List<AdditionalServiceType> getCatalog() {
        return List.of(AdditionalServiceType.values());
    }

    public BigDecimal calculateServicesTotal(Reservation reservation) {
        BigDecimal total = BigDecimal.ZERO;
        for (AdditionalService service : reservation.getServices()) {
            total = total.add(service.getTotal());
        }
        return total.setScale(2, RoundingMode.HALF_UP);
    }
}
