package com.hotel.reservations.service;

import com.hotel.reservations.model.InvoiceLine;
import com.hotel.reservations.model.InvoiceLineCategory;
import com.hotel.reservations.model.NightlyRate;
import com.hotel.reservations.model.PriceQuote;
import com.hotel.reservations.model.Reservation;
import com.hotel.reservations.model.Room;
import com.hotel.reservations.model.Season;
import com.hotel.reservations.repository.InvoiceRepository;
import com.hotel.reservations.util.SequenceGenerator;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Subsistema de facturación (FacturacionService). Construye las líneas de la factura a partir
 * de la cotización del hospedaje y de los servicios contratados.
 */
@Service
public class BillingService {

    /** IVA aplicado sobre el subtotal (19%). */
    static final BigDecimal TAX_RATE = new BigDecimal("0.19");

    private final InvoiceRepository invoiceRepository;
    private final SequenceGenerator sequenceGenerator;

    /**
     * @param invoiceRepository repositorio de facturas
     * @param sequenceGenerator generador de números de factura
     */
    public BillingService(InvoiceRepository invoiceRepository, SequenceGenerator sequenceGenerator) {
        this.invoiceRepository = invoiceRepository;
        this.sequenceGenerator = sequenceGenerator;
    }

    /**
     * Construye las líneas de la factura: una línea de hospedaje por temporada presente en la estancia
     * (en orden cronológico de primera noche) y una línea por cada servicio adicional.
     *
     * @param reservation reserva facturada
     * @param roomQuote cotización noche a noche del hospedaje
     * @return líneas de la factura, sin persistir nada
     */
    public List<InvoiceLine> buildLines(Reservation reservation, PriceQuote roomQuote) {
        return new ArrayList<>(buildRoomLines(reservation, roomQuote));
    }

    private List<InvoiceLine> buildRoomLines(Reservation reservation, PriceQuote roomQuote) {
        Map<Season, BigDecimal> priceBySeason = new LinkedHashMap<>();
        for (NightlyRate rate : roomQuote.getNightlyRates()) {
            priceBySeason.putIfAbsent(rate.getSeason(), rate.getPrice());
        }
        List<InvoiceLine> lines = new ArrayList<>();
        for (Map.Entry<Season, BigDecimal> entry : priceBySeason.entrySet()) {
            Season season = entry.getKey();
            lines.add(new InvoiceLine(
                    roomDescription(reservation.getRoom(), season),
                    InvoiceLineCategory.ROOM,
                    roomQuote.countNights(season),
                    entry.getValue().setScale(2, RoundingMode.HALF_UP)));
        }
        return lines;
    }

    private static String roomDescription(Room room, Season season) {
        return "Habitación " + room.getNumber() + " (" + room.getType().getLabel() + ") - " + season.getLabel();
    }
}
