package com.hotel.reservations.mapper;

import com.hotel.reservations.dto.ServiceCatalogItemDTO;
import com.hotel.reservations.model.AdditionalServiceType;
import com.hotel.reservations.model.ChargeMode;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class ServiceCatalogMapperTest {

    private final ServiceCatalogMapper mapper = new ServiceCatalogMapper();

    @Test
    void convierteSpaAItemDeCatalogo() {
        ServiceCatalogItemDTO dto = mapper.toDto(AdditionalServiceType.SPA);

        assertThat(dto).isEqualTo(new ServiceCatalogItemDTO(
                AdditionalServiceType.SPA,
                "Spa",
                new BigDecimal("50.00"),
                ChargeMode.FLAT,
                "Sesión de spa y relajación"));
    }

    @Test
    void desayunoSeCobraPorNoche() {
        ServiceCatalogItemDTO dto = mapper.toDto(AdditionalServiceType.BREAKFAST);

        assertThat(dto.label()).isEqualTo("Desayuno");
        assertThat(dto.unitPrice()).isEqualTo(new BigDecimal("15.00"));
        assertThat(dto.chargeMode()).isEqualTo(ChargeMode.PER_NIGHT);
    }
}
