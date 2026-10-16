package com.hotel.reservations.mapper;

import com.hotel.reservations.dto.GuestDTO;
import com.hotel.reservations.model.Guest;
import org.springframework.stereotype.Component;

/**
 * Convierte huéspedes entre el modelo de dominio y su representación DTO.
 */
@Component
public class GuestMapper {

    /**
     * @param guest huésped del dominio
     * @return DTO con los mismos datos
     */
    public GuestDTO toDto(Guest guest) {
        return new GuestDTO(guest.getFullName(), guest.getDocumentNumber(), guest.getEmail(), guest.getPhone());
    }
}
