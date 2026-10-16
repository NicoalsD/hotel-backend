package com.hotel.reservations.service;

import com.hotel.reservations.model.DigitalKey;
import com.hotel.reservations.model.Reservation;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Servicio de acceso (AccesoService). Emite llaves digitales con UUID, las revoca y valida
 * si una llave abre una habitación. Mantiene el registro de todas las llaves emitidas.
 */
@Service
public class AccessService {

    private final Map<String, DigitalKey> issuedKeys = new ConcurrentHashMap<>();

    /**
     * Emite una llave digital para la reserva. La llave no se asocia a la reserva aquí;
     * eso lo hace el check-in.
     *
     * @param reservation reserva para la que se emite la llave
     * @return llave activa para la habitación de la reserva
     */
    public DigitalKey generateKey(Reservation reservation) {
        Objects.requireNonNull(reservation, "La reserva es obligatoria");
        String code = UUID.randomUUID().toString();
        DigitalKey key = new DigitalKey(code, reservation.getRoom().getNumber(),
                reservation.getId(), LocalDateTime.now());
        issuedKeys.put(code, key);
        return key;
    }

    /**
     * Revoca la llave digital asociada a la reserva. Si la reserva no tiene llave, no hace nada.
     *
     * @param reservation reserva cuya llave se revoca
     */
    public void revokeKey(Reservation reservation) {
        Objects.requireNonNull(reservation, "La reserva es obligatoria");
        DigitalKey key = reservation.getDigitalKey();
        if (key != null) {
            key.revoke();
        }
    }

    /**
     * @param keyCode código de la llave digital
     * @param roomNumber número de la habitación a la que se quiere ingresar
     * @return {@code true} si la llave fue emitida, está activa y corresponde a esa habitación
     */
    public boolean validateAccess(String keyCode, String roomNumber) {
        if (keyCode == null) {
            return false;
        }
        DigitalKey key = issuedKeys.get(keyCode);
        return key != null && key.opens(roomNumber);
    }
}
