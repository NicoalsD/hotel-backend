package com.hotel.reservations.support;

import com.hotel.reservations.model.AdditionalService;
import com.hotel.reservations.model.AdditionalServiceType;

/**
 * Subclase de prueba de {@link AdditionalService}. La cantidad se pasa directamente,
 * así no depende de campos de la subclase durante la construcción de la clase base.
 */
public class StubAdditionalService extends AdditionalService {

    /**
     * @param id identificador del servicio
     * @param type tipo de servicio del catálogo
     * @param quantity cantidad facturable que tendrá el servicio
     */
    public StubAdditionalService(String id, AdditionalServiceType type, int quantity) {
        super(id, type, quantity);
    }

    @Override
    protected int calculateQuantity(long reservationNights) {
        return (int) reservationNights;
    }
}
