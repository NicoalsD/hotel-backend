package com.hotel.reservations.mapper;

import com.hotel.reservations.dto.RoomAvailabilityDTO;
import com.hotel.reservations.dto.RoomDTO;
import com.hotel.reservations.model.PriceQuote;
import com.hotel.reservations.model.Room;
import org.springframework.stereotype.Component;

import java.util.Objects;

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

    /**
     * @param room habitación del dominio
     * @param available si la habitación está libre para el rango consultado
     * @param quote cotización del rango, usada para noches y total estimado
     * @return DTO de disponibilidad con el costo estimado
     */
    public RoomAvailabilityDTO toAvailabilityDto(Room room, boolean available, PriceQuote quote) {
        Objects.requireNonNull(quote, "La cotización es obligatoria");
        return new RoomAvailabilityDTO(
                room.getId(),
                room.getNumber(),
                room.getType(),
                room.getType().getLabel(),
                room.getFloor(),
                room.getCapacity(),
                room.getBasePrice(),
                room.getDescription(),
                available,
                quote.getNights(),
                quote.getTotal());
    }
}
