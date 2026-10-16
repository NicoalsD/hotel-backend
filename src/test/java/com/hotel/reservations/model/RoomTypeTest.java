package com.hotel.reservations.model;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class RoomTypeTest {

    @Test
    void singleRoomMatchesContract() {
        assertThat(RoomType.SINGLE.getLabel()).isEqualTo("Sencilla");
        assertThat(RoomType.SINGLE.getBasePrice()).isEqualTo(new BigDecimal("80.00"));
        assertThat(RoomType.SINGLE.getCapacity()).isEqualTo(1);
    }

    @Test
    void doubleRoomMatchesContract() {
        assertThat(RoomType.DOUBLE.getLabel()).isEqualTo("Doble");
        assertThat(RoomType.DOUBLE.getBasePrice()).isEqualTo(new BigDecimal("120.00"));
        assertThat(RoomType.DOUBLE.getCapacity()).isEqualTo(2);
    }

    @Test
    void suiteMatchesContract() {
        assertThat(RoomType.SUITE.getLabel()).isEqualTo("Suite");
        assertThat(RoomType.SUITE.getBasePrice()).isEqualTo(new BigDecimal("250.00"));
        assertThat(RoomType.SUITE.getCapacity()).isEqualTo(4);
    }

    @Test
    void declaresThreeTypesInOrder() {
        assertThat(RoomType.values()).containsExactly(RoomType.SINGLE, RoomType.DOUBLE, RoomType.SUITE);
    }
}
