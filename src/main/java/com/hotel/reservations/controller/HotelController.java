package com.hotel.reservations.controller;

import com.hotel.reservations.facade.HotelFacade;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.hotel.reservations.dto.PriceQuoteDTO;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.GetMapping;
import java.util.List;
import java.time.LocalDate;
import com.hotel.reservations.model.RoomType;
import com.hotel.reservations.dto.RoomAvailabilityDTO;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PostMapping;
import jakarta.validation.Valid;
import com.hotel.reservations.dto.ReservationDTO;
import com.hotel.reservations.dto.ReservationRequestDTO;

/**
 * Controlador REST del hotel. Delega toda la logica en {@link HotelFacade}.
 */
@RestController
@RequestMapping("/api/hotel")
@Tag(name = "Hotel", description = "Operaciones de reservas, servicios, check-in, check-out y facturacion")
public class HotelController {

    private final HotelFacade hotelFacade;

    /**
     * @param hotelFacade fachada del sistema de reservas
     */
    public HotelController(HotelFacade hotelFacade) {
        this.hotelFacade = hotelFacade;
    }

    /**
     * Crea una reserva (caso de uso crearReserva).
     *
     * @param request datos de la reserva
     * @return reserva creada
     */
    @PostMapping("/reservar")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Crear una reserva de habitacion")
    public ReservationDTO reservar(@Valid @RequestBody ReservationRequestDTO request) {
        return hotelFacade.createReservation(request);
    }

    /**
     * Consulta la disponibilidad de habitaciones (caso de uso de consulta de disponibilidad).
     *
     * @param checkIn  fecha de llegada
     * @param checkOut fecha de salida
     * @param roomType tipo de habitacion opcional
     * @return habitaciones con su disponibilidad
     */
    @GetMapping("/disponibilidad")
    @Operation(summary = "Consultar disponibilidad de habitaciones por fechas")
    public List<RoomAvailabilityDTO> disponibilidad(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkIn,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkOut,
            @RequestParam(required = false) RoomType roomType) {
        return hotelFacade.checkAvailability(checkIn, checkOut, roomType);
    }

    /**
     * Consulta la tarifa estimada noche a noche (vista previa de precio).
     *
     * @param roomType tipo de habitacion
     * @param checkIn  fecha de llegada
     * @param checkOut fecha de salida
     * @return cotizacion detallada
     */
    @GetMapping("/tarifa")
    @Operation(summary = "Consultar tarifa estimada para un tipo de habitacion y fechas")
    public PriceQuoteDTO tarifa(
            @RequestParam RoomType roomType,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkIn,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkOut) {
        return hotelFacade.quotePrice(roomType, checkIn, checkOut);
    }
}
