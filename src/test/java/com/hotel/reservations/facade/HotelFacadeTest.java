package com.hotel.reservations.facade;

import com.hotel.reservations.dto.GuestDTO;
import com.hotel.reservations.dto.InvoiceDTO;
import com.hotel.reservations.dto.ReservationDTO;
import com.hotel.reservations.dto.ReservationRequestDTO;
import com.hotel.reservations.factory.AdditionalServiceFactory;
import com.hotel.reservations.mapper.GuestMapper;
import com.hotel.reservations.mapper.InvoiceMapper;
import com.hotel.reservations.mapper.PriceQuoteMapper;
import com.hotel.reservations.mapper.ReservationMapper;
import com.hotel.reservations.mapper.RoomMapper;
import com.hotel.reservations.mapper.ServiceCatalogMapper;
import com.hotel.reservations.model.AdditionalServiceType;
import com.hotel.reservations.model.Room;
import com.hotel.reservations.model.RoomType;
import com.hotel.reservations.pricing.HighSeasonPricingStrategy;
import com.hotel.reservations.pricing.LowSeasonPricingStrategy;
import com.hotel.reservations.pricing.PricingStrategyResolver;
import com.hotel.reservations.pricing.SeasonCalendar;
import com.hotel.reservations.repository.InMemoryInvoiceRepository;
import com.hotel.reservations.repository.InMemoryReservationRepository;
import com.hotel.reservations.repository.InMemoryRoomRepository;
import com.hotel.reservations.service.AccessService;
import com.hotel.reservations.service.AdditionalServiceService;
import com.hotel.reservations.service.BillingService;
import com.hotel.reservations.service.RateService;
import com.hotel.reservations.service.RoomService;
import com.hotel.reservations.util.SequenceGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Prueba de integracion de la fachada con objetos reales (sin contexto de Spring).
 */
class HotelFacadeTest {

    private static final LocalDate CHECK_IN = LocalDate.of(2027, 3, 10);
    private static final LocalDate CHECK_OUT = LocalDate.of(2027, 3, 13);

    private HotelFacade facade;

    @BeforeEach
    void setUp() {
        InMemoryRoomRepository roomRepository = new InMemoryRoomRepository();
        InMemoryReservationRepository reservationRepository = new InMemoryReservationRepository();
        InMemoryInvoiceRepository invoiceRepository = new InMemoryInvoiceRepository();
        roomRepository.save(new Room(7L, "201", RoomType.DOUBLE, 2, "Habitación doble con vista a la ciudad"));
        roomRepository.save(new Room(1L, "101", RoomType.SINGLE, 1, "Habitación sencilla interior"));

        SequenceGenerator sequenceGenerator = new SequenceGenerator();
        SeasonCalendar seasonCalendar = new SeasonCalendar();
        PricingStrategyResolver resolver = new PricingStrategyResolver(
                List.of(new HighSeasonPricingStrategy(), new LowSeasonPricingStrategy()));
        RoomMapper roomMapper = new RoomMapper();
        GuestMapper guestMapper = new GuestMapper();
        facade = new HotelFacade(
                new RoomService(roomRepository, reservationRepository),
                new RateService(seasonCalendar, resolver),
                new AdditionalServiceService(new AdditionalServiceFactory()),
                new BillingService(invoiceRepository, sequenceGenerator),
                new AccessService(),
                reservationRepository,
                sequenceGenerator,
                guestMapper,
                roomMapper,
                new ReservationMapper(roomMapper, guestMapper),
                new InvoiceMapper(guestMapper),
                new PriceQuoteMapper(),
                new ServiceCatalogMapper());
    }

    @Test
    void flujoCompletoReservaServicioCheckInCheckOutYFactura() {
        ReservationDTO reservation = facade.createReservation(request(7L, CHECK_IN, CHECK_OUT));
        assertThat(reservation.status().name()).isEqualTo("CONFIRMED");
        assertThat(reservation.roomTotal()).isEqualByComparingTo("360.00");

        ReservationDTO withService = facade.addService(reservation.id(), AdditionalServiceType.BREAKFAST);
        assertThat(withService.servicesTotal()).isEqualByComparingTo("45.00");

        ReservationDTO checkedIn = facade.performCheckIn(reservation.id());
        assertThat(checkedIn.status().name()).isEqualTo("CHECKED_IN");
        assertThat(checkedIn.digitalKey()).isNotNull();

        InvoiceDTO invoice = facade.performCheckOut(reservation.id());
        assertThat(invoice.roomSubtotal()).isEqualByComparingTo("360.00");
        assertThat(invoice.servicesSubtotal()).isEqualByComparingTo("45.00");
        assertThat(invoice.subtotal()).isEqualByComparingTo("405.00");
        assertThat(invoice.taxAmount()).isEqualByComparingTo("76.95");
        assertThat(invoice.total()).isEqualByComparingTo(new BigDecimal("481.95"));
        assertThat(facade.getInvoice(reservation.id()).invoiceNumber()).isEqualTo(invoice.invoiceNumber());
        assertThat(facade.getReservation(reservation.id()).status().name()).isEqualTo("CHECKED_OUT");
    }

    @Test
    void reservaTraslapadaEnLaMismaHabitacionLanzaRoomNotAvailableException() {
        facade.createReservation(request(7L, CHECK_IN, CHECK_OUT));

        assertThatThrownBy(() -> facade.createReservation(
                request(7L, CHECK_IN.plusDays(1), CHECK_OUT.plusDays(1))))
                .isInstanceOf(com.hotel.reservations.exception.RoomNotAvailableException.class);
    }

    @Test
    void checkOutAntesDeCheckInLanzaInvalidReservationStateException() {
        ReservationDTO reservation = facade.createReservation(request(7L, CHECK_IN, CHECK_OUT));

        assertThatThrownBy(() -> facade.performCheckOut(reservation.id()))
                .isInstanceOf(com.hotel.reservations.exception.InvalidReservationStateException.class);
    }

    private static ReservationRequestDTO request(Long roomId, LocalDate checkIn, LocalDate checkOut) {
        return new ReservationRequestDTO(roomId, checkIn, checkOut,
                new GuestDTO("Ana Pérez", "1020304050", "ana@correo.com", "3001234567"));
    }
}
