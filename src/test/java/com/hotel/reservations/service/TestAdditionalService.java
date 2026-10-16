package com.hotel.reservations.service;

import com.hotel.reservations.model.AdditionalService;
import com.hotel.reservations.model.AdditionalServiceType;
import com.hotel.reservations.model.ChargeMode;

/**
 * Servicio adicional concreto solo para pruebas. Calcula la cantidad según la forma de cobro
 * del tipo: {@link ChargeMode#FLAT} factura 1 y {@link ChargeMode#PER_NIGHT} factura una vez por noche.
 * No depende de las subclases de producción del patrón Factory.
 */
class TestAdditionalService extends AdditionalService {

    /**
     * @param id identificador del servicio de prueba
     * @param type tipo de servicio del catálogo
     * @param reservationNights noches de la reserva a la que se agrega
     */
    TestAdditionalService(String id, AdditionalServiceType type, long reservationNights) {
        super(id, type, reservationNights);
    }

    /** Usa solo el tipo (asignado antes de este cálculo), nunca campos propios. */
    @Override
    protected int calculateQuantity(long reservationNights) {
        return getType().getChargeMode() == ChargeMode.PER_NIGHT ? (int) reservationNights : 1;
    }
}
