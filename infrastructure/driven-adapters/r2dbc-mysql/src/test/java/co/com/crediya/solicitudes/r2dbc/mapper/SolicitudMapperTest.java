package co.com.crediya.solicitudes.r2dbc.mapper;

import co.com.crediya.solicitudes.model.solicitud.Estado;
import co.com.crediya.solicitudes.model.solicitud.Solicitud;
import co.com.crediya.solicitudes.r2dbc.entity.SolicitudEntity;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class SolicitudMapperTest {

    @Test
    void toEntity_y_toDomain_ok() {
        UUID id = UUID.randomUUID();
        var s = Solicitud.builder()
                .id(id)
                .email("a@b.com")
                .monto(new BigDecimal("1000"))
                .plazoMeses(12)
                .tipoPrestamoId(UUID.randomUUID())
                .estado(Estado.PENDIENTE_REVISION)
                .created(Instant.now())
                .build();

        var mapper = new SolicitudMapper();
        SolicitudEntity e = mapper.toEntity(s);
        assertEquals(id.toString(), e.getId());
        assertEquals(s.getEmail(), e.getEmail());
        assertEquals(EstadoDbMapper.ID_PENDIENTE, e.getIdEstado());

        Solicitud s2 = mapper.toDomain(e);
        assertEquals(s.getEmail(), s2.getEmail());
        assertEquals(Estado.PENDIENTE_REVISION, s2.getEstado());
    }
}
