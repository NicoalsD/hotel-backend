package com.hotel.reservations.model;


import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class DigitalKeyTest {

    private final LocalDateTime issuedAt = LocalDateTime.of(2026, 11, 1, 15, 0);

    private DigitalKey newKey() {
        return new DigitalKey("code-123", "201", "RES-1001", issuedAt);
    }

    @Test
    void keyIsActiveOnCreation() {
        DigitalKey key = newKey();

        assertThat(key.isActive()).isTrue();
        assertThat(key.getCode()).isEqualTo("code-123");
        assertThat(key.getRoomNumber()).isEqualTo("201");
        assertThat(key.getReservationId()).isEqualTo("RES-1001");
        assertThat(key.getIssuedAt()).isEqualTo(issuedAt);
    }

    @Test
    void activeKeyOpensItsOwnRoomOnly() {
        DigitalKey key = newKey();

        assertThat(key.opens("201")).isTrue();
        assertThat(key.opens("202")).isFalse();
    }

    @Test
    void revokedKeyOpensNothing() {
        DigitalKey key = newKey();

        key.revoke();

        assertThat(key.isActive()).isFalse();
        assertThat(key.opens("201")).isFalse();
    }

    @Test
    void restoreKeepsPersistedDataAndActiveFlag() {
        LocalDateTime issuedAt = LocalDateTime.of(2026, 3, 1, 14, 0);

        DigitalKey active = DigitalKey.restore("code-1", "101", "RES-1001", issuedAt, true);
        DigitalKey revoked = DigitalKey.restore("code-2", "102", "RES-1002", issuedAt, false);

        assertThat(active.getCode()).isEqualTo("code-1");
        assertThat(active.getIssuedAt()).isEqualTo(issuedAt);
        assertThat(active.isActive()).isTrue();
        assertThat(active.opens("101")).isTrue();
        assertThat(revoked.isActive()).isFalse();
        assertThat(revoked.opens("102")).isFalse();
    }
}
