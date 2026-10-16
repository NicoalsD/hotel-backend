package com.hotel.reservations.controller;

import com.hotel.reservations.facade.HotelFacade;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
