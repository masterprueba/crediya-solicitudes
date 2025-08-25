package co.com.crediya.solicitudes.r2dbc.mapper;

import co.com.crediya.solicitudes.model.solicitud.Estado;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class EstadoDbMapperTest {

    @Test
    void toDb_y_toDomain_ok() {
        assertEquals(EstadoDbMapper.ID_PENDIENTE, EstadoDbMapper.toDb(Estado.PENDIENTE_REVISION));
        assertEquals(Estado.PENDIENTE_REVISION, EstadoDbMapper.toDomain(EstadoDbMapper.ID_PENDIENTE));

        assertEquals(EstadoDbMapper.ID_APROBADA, EstadoDbMapper.toDb(Estado.APROBADA));
        assertEquals(Estado.APROBADA, EstadoDbMapper.toDomain(EstadoDbMapper.ID_APROBADA));

        assertEquals(EstadoDbMapper.ID_RECHAZADA, EstadoDbMapper.toDb(Estado.RECHAZADA));
        assertEquals(Estado.RECHAZADA, EstadoDbMapper.toDomain(EstadoDbMapper.ID_RECHAZADA));
    }

    @Test
    void toDomain_desconocido_lanzaExcepcion() {
        var ex = assertThrows(IllegalArgumentException.class, () -> EstadoDbMapper.toDomain(99));
        assertTrue(ex.getMessage().contains("estado_id_desconocido"));
    }
}
