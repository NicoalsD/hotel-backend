package com.hotel.reservations.mapper;

import com.hotel.reservations.dto.GuestDTO;
import com.hotel.reservations.model.Guest;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class GuestMapperTest {

    private final GuestMapper mapper = new GuestMapper();

    @Test
    void convierteHuespedADto() {
        Guest guest = new Guest("Ana Pérez", "1020304050", "ana@correo.com", "3001234567");

        GuestDTO dto = mapper.toDto(guest);

        assertThat(dto).isEqualTo(new GuestDTO("Ana Pérez", "1020304050", "ana@correo.com", "3001234567"));
    }

    @Test
    void convierteDtoADominio() {
        GuestDTO dto = new GuestDTO("Luis Gómez", "900800700", "luis@correo.com", "3109876543");

        Guest guest = mapper.toDomain(dto);

        assertThat(guest.getFullName()).isEqualTo("Luis Gómez");
        assertThat(guest.getDocumentNumber()).isEqualTo("900800700");
        assertThat(guest.getEmail()).isEqualTo("luis@correo.com");
        assertThat(guest.getPhone()).isEqualTo("3109876543");
    }
}
