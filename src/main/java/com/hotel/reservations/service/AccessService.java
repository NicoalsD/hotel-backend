package com.hotel.reservations.service;

import com.hotel.reservations.model.DigitalKey;
import com.hotel.reservations.model.Reservation;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class AccessService {

    private final Map<String, DigitalKey> issuedKeys = new ConcurrentHashMap<>();

    public DigitalKey generateKey(Reservation reservation) {
        String code = UUID.randomUUID().toString();
        DigitalKey key = new DigitalKey(code, reservation.getRoom().getNumber(),
                reservation.getId(), LocalDateTime.now());
        issuedKeys.put(code, key);
        return key;
    }

    public void revokeKey(Reservation reservation) {
        DigitalKey key = reservation.getDigitalKey();
        if (key != null) {
            key.revoke();
        }
    }

    public boolean validateAccess(String keyCode, String roomNumber) {
        DigitalKey key = issuedKeys.get(keyCode);
        return key != null && key.opens(roomNumber);
    }
}
