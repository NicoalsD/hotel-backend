package com.hotel.reservations.mapper;

import com.hotel.reservations.dto.ServiceCatalogItemDTO;
import com.hotel.reservations.model.AdditionalServiceType;
import org.springframework.stereotype.Component;

/**
 * Convierte tipos de servicio del catálogo a su representación DTO.
 */
@Component
public class ServiceCatalogMapper {

    /**
     * @param type tipo de servicio del catálogo
     * @return DTO con precio, modo de cobro y descripción
     */
    public ServiceCatalogItemDTO toDto(AdditionalServiceType type) {
        return new ServiceCatalogItemDTO(
                type,
                type.getLabel(),
                type.getUnitPrice(),
                type.getChargeMode(),
                type.getDescription());
    }
}
