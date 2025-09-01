package co.com.crediya.solicitudes.r2dbc.entity;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

class SolicitudEntityTest {

    @Test
    void gettersAndSettersShouldWork() {
        SolicitudEntity entity = new SolicitudEntity();
        String id = "sol-1";
        String email = "test@crediya.com";
        BigDecimal monto = new BigDecimal("10000.50");
        Integer plazoMeses = 12;
        Integer idEstado = 2;
        String tipoPrestamoId = "TP-01";
        Instant created = Instant.now();

        entity.setId(id);
        entity.setEmail(email);
        entity.setMonto(monto);
        entity.setPlazoMeses(plazoMeses);
        entity.setIdEstado(idEstado);
        entity.setTipoPrestamoId(tipoPrestamoId);
        entity.setCreated(created);

        assertThat(entity.getId()).isEqualTo(id);
        assertThat(entity.getEmail()).isEqualTo(email);
        assertThat(entity.getMonto()).isEqualTo(monto);
        assertThat(entity.getPlazoMeses()).isEqualTo(plazoMeses);
        assertThat(entity.getIdEstado()).isEqualTo(idEstado);
        assertThat(entity.getTipoPrestamoId()).isEqualTo(tipoPrestamoId);
        assertThat(entity.getCreated()).isEqualTo(created);
    }
}