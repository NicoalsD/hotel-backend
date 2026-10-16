package com.hotel.reservations.mapper;

import com.hotel.reservations.dto.AdditionalServiceDTO;
import com.hotel.reservations.dto.DigitalKeyDTO;
import com.hotel.reservations.dto.ReservationDTO;
import com.hotel.reservations.model.AdditionalService;
import com.hotel.reservations.model.DigitalKey;
import com.hotel.reservations.model.Reservation;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Convierte reservas del dominio a su representación DTO, incluyendo servicios y llave digital.
 */
@Component
public class ReservationMapper {

    private final RoomMapper roomMapper;
    private final GuestMapper guestMapper;

    /**
     * @param roomMapper conversor de habitaciones
     * @param guestMapper conversor de huéspedes
     */
    public ReservationMapper(RoomMapper roomMapper, GuestMapper guestMapper) {
        this.roomMapper = roomMapper;
        this.guestMapper = guestMapper;
    }

    /**
     * @param reservation reserva del dominio
     * @return DTO completo de la reserva
     */
    public ReservationDTO toDto(Reservation reservation) {
        List<AdditionalServiceDTO> services = reservation.getServices().stream()
                .map(this::toServiceDto)
                .toList();
        DigitalKey key = reservation.getDigitalKey();
        return new ReservationDTO(
                reservation.getId(),
                reservation.getStatus(),
                reservation.getStatus().getLabel(),
                guestMapper.toDto(reservation.getGuest()),
                roomMapper.toDto(reservation.getRoom()),
                reservation.getRange().getCheckIn(),
                reservation.getRange().getCheckOut(),
                reservation.getNights(),
                reservation.getRoomTotal(),
                services,
                reservation.getServicesTotal(),
                reservation.getEstimatedTotal(),
                key == null ? null : toDigitalKeyDto(key),
                reservation.getInvoiceNumber(),
                reservation.getCreatedAt(),
                reservation.getCheckedInAt(),
                reservation.getCheckedOutAt());
    }

    private AdditionalServiceDTO toServiceDto(AdditionalService service) {
        return new AdditionalServiceDTO(
                service.getId(),
                service.getType(),
                service.getLabel(),
                service.getType().getChargeMode(),
                service.getUnitPrice(),
                service.getQuantity(),
                service.getTotal(),
                service.getAddedAt());
    }

    private DigitalKeyDTO toDigitalKeyDto(DigitalKey key) {
        return new DigitalKeyDTO(key.getCode(), key.getRoomNumber(), key.getIssuedAt(), key.isActive());
    }
}
