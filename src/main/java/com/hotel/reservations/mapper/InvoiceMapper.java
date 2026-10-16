package com.hotel.reservations.mapper;

import com.hotel.reservations.dto.InvoiceDTO;
import com.hotel.reservations.dto.InvoiceLineDTO;
import com.hotel.reservations.model.DigitalKey;
import com.hotel.reservations.model.Invoice;
import com.hotel.reservations.model.InvoiceLine;
import com.hotel.reservations.model.Reservation;
import com.hotel.reservations.model.Room;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Convierte facturas del dominio a su representación DTO.
 */
@Component
public class InvoiceMapper {

    private final GuestMapper guestMapper;

    /**
     * @param guestMapper conversor de huéspedes
     */
    public InvoiceMapper(GuestMapper guestMapper) {
        this.guestMapper = guestMapper;
    }

    /**
     * @param invoice factura del dominio
     * @return DTO con líneas, subtotales e impuestos
     */
    public InvoiceDTO toDto(Invoice invoice) {
        Reservation reservation = invoice.getReservation();
        Room room = reservation.getRoom();
        DigitalKey key = reservation.getDigitalKey();
        List<InvoiceLineDTO> lines = invoice.getLines().stream()
                .map(this::toLineDto)
                .toList();
        return new InvoiceDTO(
                invoice.getInvoiceNumber(),
                reservation.getId(),
                invoice.getIssuedAt(),
                guestMapper.toDto(reservation.getGuest()),
                room.getNumber(),
                room.getType(),
                room.getType().getLabel(),
                reservation.getRange().getCheckIn(),
                reservation.getRange().getCheckOut(),
                reservation.getNights(),
                lines,
                invoice.getRoomSubtotal(),
                invoice.getServicesSubtotal(),
                invoice.getSubtotal(),
                invoice.getTaxRate(),
                invoice.getTaxAmount(),
                invoice.getTotal(),
                key == null ? null : key.getCode());
    }

    private InvoiceLineDTO toLineDto(InvoiceLine line) {
        return new InvoiceLineDTO(
                line.getDescription(),
                line.getCategory(),
                line.getQuantity(),
                line.getUnitPrice(),
                line.getAmount());
    }
}
