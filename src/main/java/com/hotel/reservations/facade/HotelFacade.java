package com.hotel.reservations.facade;

import com.hotel.reservations.dto.InvoiceDTO;
import com.hotel.reservations.dto.PriceQuoteDTO;
import com.hotel.reservations.dto.ReservationDTO;
import com.hotel.reservations.dto.ReservationRequestDTO;
import com.hotel.reservations.dto.RoomAvailabilityDTO;
import com.hotel.reservations.dto.RoomDTO;
import com.hotel.reservations.dto.ServiceCatalogItemDTO;
import com.hotel.reservations.exception.InvalidDateRangeException;
import com.hotel.reservations.exception.InvalidReservationStateException;
import com.hotel.reservations.exception.ResourceNotFoundException;
import com.hotel.reservations.mapper.GuestMapper;
import com.hotel.reservations.mapper.InvoiceMapper;
import com.hotel.reservations.mapper.PriceQuoteMapper;
import com.hotel.reservations.mapper.ReservationMapper;
import com.hotel.reservations.mapper.RoomMapper;
import com.hotel.reservations.mapper.ServiceCatalogMapper;
import com.hotel.reservations.model.AdditionalServiceType;
import com.hotel.reservations.model.DateRange;
import com.hotel.reservations.model.DigitalKey;
import com.hotel.reservations.model.Invoice;
import com.hotel.reservations.model.PriceQuote;
import com.hotel.reservations.model.Reservation;
import com.hotel.reservations.model.ReservationStatus;
import com.hotel.reservations.model.Room;
import com.hotel.reservations.model.RoomType;
import com.hotel.reservations.repository.ReservationRepository;
import com.hotel.reservations.service.AccessService;
import com.hotel.reservations.service.AdditionalServiceService;
import com.hotel.reservations.service.BillingService;
import com.hotel.reservations.service.RateService;
import com.hotel.reservations.service.RoomService;
import com.hotel.reservations.util.SequenceGenerator;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Fachada del sistema de reservas del hotel (patron Facade).
 * Es el unico punto de entrada que usa el controlador: orquesta los subsistemas
 * de habitaciones, tarifas, servicios adicionales, facturacion y acceso, y convierte
 * los objetos de dominio a DTO mediante los mappers.
 */
@Service
public class HotelFacade {

    private final RoomService roomService;
    private final RateService rateService;
    private final AdditionalServiceService additionalServiceService;
    private final BillingService billingService;
    private final AccessService accessService;
    private final ReservationRepository reservationRepository;
    private final SequenceGenerator sequenceGenerator;
    private final GuestMapper guestMapper;
    private final RoomMapper roomMapper;
    private final ReservationMapper reservationMapper;
    private final InvoiceMapper invoiceMapper;
    private final PriceQuoteMapper priceQuoteMapper;
    private final ServiceCatalogMapper serviceCatalogMapper;

    /**
     * @param roomService             servicio de habitaciones
     * @param rateService             servicio de tarifas
     * @param additionalServiceService servicio de servicios adicionales
     * @param billingService          servicio de facturacion
     * @param accessService           servicio de acceso digital
     * @param reservationRepository   repositorio de reservas
     * @param sequenceGenerator       generador de identificadores
     * @param guestMapper             mapper de huespedes
     * @param roomMapper              mapper de habitaciones
     * @param reservationMapper       mapper de reservas
     * @param invoiceMapper           mapper de facturas
     * @param priceQuoteMapper        mapper de cotizaciones
     * @param serviceCatalogMapper    mapper del catalogo de servicios
     */
    public HotelFacade(RoomService roomService,
                       RateService rateService,
                       AdditionalServiceService additionalServiceService,
                       BillingService billingService,
                       AccessService accessService,
                       ReservationRepository reservationRepository,
                       SequenceGenerator sequenceGenerator,
                       GuestMapper guestMapper,
                       RoomMapper roomMapper,
                       ReservationMapper reservationMapper,
                       InvoiceMapper invoiceMapper,
                       PriceQuoteMapper priceQuoteMapper,
                       ServiceCatalogMapper serviceCatalogMapper) {
        this.roomService = roomService;
        this.rateService = rateService;
        this.additionalServiceService = additionalServiceService;
        this.billingService = billingService;
        this.accessService = accessService;
        this.reservationRepository = reservationRepository;
        this.sequenceGenerator = sequenceGenerator;
        this.guestMapper = guestMapper;
        this.roomMapper = roomMapper;
        this.reservationMapper = reservationMapper;
        this.invoiceMapper = invoiceMapper;
        this.priceQuoteMapper = priceQuoteMapper;
        this.serviceCatalogMapper = serviceCatalogMapper;
    }

    /**
     * Caso de uso "crearReserva": valida fechas, reserva la habitacion y guarda la reserva.
     *
     * @param request datos de la reserva solicitada
     * @return reserva creada
     * @throws InvalidDateRangeException si la llegada es anterior a hoy o el rango es invalido
     */
    public ReservationDTO createReservation(ReservationRequestDTO request) {
        if (request.checkIn().isBefore(LocalDate.now())) {
            throw new InvalidDateRangeException("La fecha de llegada no puede ser anterior a la fecha actual");
        }
        DateRange range = new DateRange(request.checkIn(), request.checkOut());
        Room room = roomService.findById(request.roomId());
        PriceQuote quote = rateService.calculateQuote(room.getType(), range);
        Reservation reservation = new Reservation(
                sequenceGenerator.nextReservationId(),
                guestMapper.toDomain(request.guest()),
                room,
                range,
                quote.getTotal());
        roomService.reserveRoom(room.getId(), reservation.getId(), range);
        return reservationMapper.toDto(reservationRepository.save(reservation));
    }

    /**
     * Caso de uso "agregarServicio": agrega un servicio adicional a la reserva.
     *
     * @param reservationId identificador de la reserva
     * @param type          tipo de servicio
     * @return reserva actualizada
     */
    public ReservationDTO addService(String reservationId, AdditionalServiceType type) {
        Reservation reservation = findReservation(reservationId);
        additionalServiceService.addService(reservation, type);
        return reservationMapper.toDto(reservationRepository.save(reservation));
    }

    /**
     * Caso de uso "realizarCheckIn": genera la llave digital, registra el ingreso y ocupa la habitacion.
     *
     * @param reservationId identificador de la reserva
     * @return reserva con check-in realizado
     */
    public ReservationDTO performCheckIn(String reservationId) {
        Reservation reservation = findReservation(reservationId);
        DigitalKey key = accessService.generateKey(reservation);
        reservation.checkIn(key);
        roomService.occupyRoom(reservation.getRoom().getId());
        return reservationMapper.toDto(reservationRepository.save(reservation));
    }

    /**
     * Caso de uso "realizarCheckOut": calcula cargos, emite la factura, revoca la llave
     * y libera la habitacion.
     *
     * @param reservationId identificador de la reserva
     * @return factura emitida
     * @throws InvalidReservationStateException si la reserva no esta con check-in realizado
     */
    public InvoiceDTO performCheckOut(String reservationId) {
        Reservation reservation = findReservation(reservationId);
        if (reservation.getStatus() != ReservationStatus.CHECKED_IN) {
            throw new InvalidReservationStateException("La reserva " + reservation.getId()
                    + " no puede hacer check-out porque su estado es " + reservation.getStatus().getLabel());
        }
        PriceQuote quote = rateService.calculateQuote(reservation.getRoom().getType(), reservation.getRange());
        Invoice invoice = billingService.generateInvoice(reservation, quote);
        reservation.checkOut(invoice.getInvoiceNumber());
        accessService.revokeKey(reservation);
        roomService.releaseRoom(reservation.getRoom().getId(), reservation.getId());
        reservationRepository.save(reservation);
        return invoiceMapper.toDto(invoice);
    }

    /**
     * Consulta una reserva por identificador.
     *
     * @param reservationId identificador de la reserva
     * @return reserva encontrada
     * @throws ResourceNotFoundException si no existe la reserva
     */
    public ReservationDTO getReservation(String reservationId) {
        return reservationMapper.toDto(findReservation(reservationId));
    }

    /**
     * Lista todas las reservas, de la mas reciente a la mas antigua.
     *
     * @return reservas del hotel
     */
    public List<ReservationDTO> listReservations() {
        return reservationRepository.findAll().stream()
                .map(reservationMapper::toDto)
                .toList();
    }

    /**
     * Consulta la disponibilidad y el precio estimado de las habitaciones en un rango de fechas.
     *
     * @param checkIn  fecha de llegada
     * @param checkOut fecha de salida
     * @param typeOrNull tipo de habitacion opcional (null para todos)
     * @return habitaciones con su disponibilidad y cotizacion
     */
    public List<RoomAvailabilityDTO> checkAvailability(LocalDate checkIn, LocalDate checkOut, RoomType typeOrNull) {
        DateRange range = new DateRange(checkIn, checkOut);
        Map<RoomType, PriceQuote> quotes = new HashMap<>();
        return roomService.findByType(typeOrNull).stream()
                .map(room -> {
                    PriceQuote quote = quotes.computeIfAbsent(room.getType(),
                            type -> rateService.calculateQuote(type, range));
                    boolean available = roomService.isRoomAvailable(room.getId(), range);
                    return roomMapper.toAvailabilityDto(room, available, quote);
                })
                .toList();
    }

    /**
     * Calcula la cotizacion noche a noche para un tipo de habitacion.
     *
     * @param roomType tipo de habitacion
     * @param checkIn  fecha de llegada
     * @param checkOut fecha de salida
     * @return cotizacion detallada
     */
    public PriceQuoteDTO quotePrice(RoomType roomType, LocalDate checkIn, LocalDate checkOut) {
        return priceQuoteMapper.toDto(rateService.calculateQuote(roomType, new DateRange(checkIn, checkOut)));
    }

    /**
     * Lista todas las habitaciones del hotel.
     *
     * @return habitaciones
     */
    public List<RoomDTO> listRooms() {
        return roomService.findAll().stream()
                .map(roomMapper::toDto)
                .toList();
    }

    /**
     * Lista el catalogo de servicios adicionales disponibles.
     *
     * @return catalogo de servicios
     */
    public List<ServiceCatalogItemDTO> getServiceCatalog() {
        return additionalServiceService.getCatalog().stream()
                .map(serviceCatalogMapper::toDto)
                .toList();
    }

    /**
     * Consulta la factura emitida para una reserva.
     *
     * @param reservationId identificador de la reserva
     * @return factura de la reserva
     */
    public InvoiceDTO getInvoice(String reservationId) {
        return invoiceMapper.toDto(billingService.findByReservationId(reservationId));
    }

    private Reservation findReservation(String reservationId) {
        return reservationRepository.findById(reservationId)
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró la reserva " + reservationId));
    }
}
