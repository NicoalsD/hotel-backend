package com.hotel.reservations.service;

import com.hotel.reservations.model.DateRange;
import com.hotel.reservations.model.Guest;
import com.hotel.reservations.model.InvoiceLine;
import com.hotel.reservations.model.InvoiceLineCategory;
import com.hotel.reservations.model.NightlyRate;
import com.hotel.reservations.model.PriceQuote;
import com.hotel.reservations.model.Reservation;
import com.hotel.reservations.model.Room;
import com.hotel.reservations.model.RoomType;
import com.hotel.reservations.model.Season;
import com.hotel.reservations.repository.InMemoryInvoiceRepository;
import com.hotel.reservations.util.SequenceGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class BillingServiceTest {

    private static final List<Integer> HIGH_MONTHS = List.of(6, 7, 12, 1);

    private BillingService billingService;
    private Room room201;
    private Guest guest;

    @BeforeEach
    void setUp() {
        billingService = new BillingService(new InMemoryInvoiceRepository(), new SequenceGenerator());
        room201 = new Room(7L, "201", RoomType.DOUBLE, 2, "Habitación doble con vista a la ciudad");
        guest = new Guest("Ana Pérez", "1020304050", "ana@correo.com", "3001234567");
    }

    /** Cotización de prueba: temporada alta en junio, julio, diciembre y enero (x1.5), resto baja. */
    private static PriceQuote quoteFor(RoomType type, DateRange range) {
        List<NightlyRate> rates = new ArrayList<>();
        for (LocalDate date : range.getNightDates()) {
            boolean high = HIGH_MONTHS.contains(date.getMonthValue());
            Season season = high ? Season.HIGH : Season.LOW;
            BigDecimal price = type.getBasePrice().multiply(season.getMultiplier()).setScale(2);
            rates.add(new NightlyRate(date, season, price));
        }
        return new PriceQuote(type, range, rates);
    }

    private Reservation reservation(DateRange range, PriceQuote quote) {
        return new Reservation("RES-1001", guest, room201, range, quote.getTotal());
    }

    private static DateRange range(String checkIn, String checkOut) {
        return new DateRange(LocalDate.parse(checkIn), LocalDate.parse(checkOut));
    }

    @Test
    void mixedSeasonStayProducesOneRoomLinePerSeasonInChronologicalOrder() {
        DateRange range = range("2026-11-29", "2026-12-02");
        PriceQuote quote = quoteFor(RoomType.DOUBLE, range);

        List<InvoiceLine> lines = billingService.buildLines(reservation(range, quote), quote);

        assertThat(lines).hasSize(2);
        InvoiceLine low = lines.get(0);
        assertThat(low.getDescription()).isEqualTo("Habitación 201 (Doble) - Temporada baja");
        assertThat(low.getCategory()).isEqualTo(InvoiceLineCategory.ROOM);
        assertThat(low.getQuantity()).isEqualTo(2);
        assertThat(low.getUnitPrice()).isEqualByComparingTo("120.00");
        assertThat(low.getAmount()).isEqualByComparingTo("240.00");

        InvoiceLine high = lines.get(1);
        assertThat(high.getDescription()).isEqualTo("Habitación 201 (Doble) - Temporada alta");
        assertThat(high.getQuantity()).isEqualTo(1);
        assertThat(high.getUnitPrice()).isEqualByComparingTo("180.00");
        assertThat(high.getAmount()).isEqualByComparingTo("180.00");
    }

    @Test
    void highSeasonOnlyStayProducesSingleLine() {
        DateRange range = range("2026-12-30", "2027-01-02");
        PriceQuote quote = quoteFor(RoomType.DOUBLE, range);

        List<InvoiceLine> lines = billingService.buildLines(reservation(range, quote), quote);

        assertThat(lines).hasSize(1);
        assertThat(lines.get(0).getDescription()).isEqualTo("Habitación 201 (Doble) - Temporada alta");
        assertThat(lines.get(0).getQuantity()).isEqualTo(3);
        assertThat(lines.get(0).getAmount()).isEqualByComparingTo("540.00");
    }

    @Test
    void seasonLinesFollowOrderOfFirstNight() {
        DateRange range = range("2026-12-31", "2027-02-02");
        PriceQuote quote = quoteFor(RoomType.DOUBLE, range);

        List<InvoiceLine> lines = billingService.buildLines(reservation(range, quote), quote);

        assertThat(lines).extracting(InvoiceLine::getDescription).containsExactly(
                "Habitación 201 (Doble) - Temporada alta",
                "Habitación 201 (Doble) - Temporada baja");
        assertThat(lines).extracting(InvoiceLine::getQuantity).containsExactly(32L, 1L);
    }

    @Test
    void roomLinesAmountsEqualSeasonSubtotalsOfQuote() {
        DateRange range = range("2026-11-29", "2026-12-02");
        PriceQuote quote = quoteFor(RoomType.DOUBLE, range);

        List<InvoiceLine> lines = billingService.buildLines(reservation(range, quote), quote);
        BigDecimal roomAmounts = lines.stream()
                .map(InvoiceLine::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        assertThat(roomAmounts).isEqualByComparingTo(quote.getTotal());
        assertThat(lines.get(0).getAmount()).isEqualByComparingTo(quote.getSubtotalFor(Season.LOW));
        assertThat(lines.get(1).getAmount()).isEqualByComparingTo(quote.getSubtotalFor(Season.HIGH));
    }
}
