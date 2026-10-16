package com.hotel.reservations.mapper;

import com.hotel.reservations.dto.RoomDTO;
import com.hotel.reservations.model.Room;
import org.springframework.stereotype.Component;

/**
 * Convierte habitaciones del dominio a su representación DTO.
 */
@Component
public class RoomMapper {

    /**
     * @param room habitación del dominio
     * @return DTO con etiquetas en español
     */
    public RoomDTO toDto(Room room) {
        return new RoomDTO(
                room.getId(),
                room.getNumber(),
                room.getType(),
                room.getType().getLabel(),
                room.getFloor(),
                room.getCapacity(),
                room.getBasePrice(),
                room.getDescription(),
                room.getStatus(),
                room.getStatus().getLabel());
    }
}
