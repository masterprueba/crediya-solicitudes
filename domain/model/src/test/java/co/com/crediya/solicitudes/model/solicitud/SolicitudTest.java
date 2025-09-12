package co.com.crediya.solicitudes.model.solicitud;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DisplayName("Solicitud Test")
class SolicitudTest {

    @Test
    @DisplayName("Crear Solicitud - Exitoso")
    void testCrearSolicitudExitoso() {
        Solicitud solicitud = Solicitud.builder()
                .id(java.util.UUID.randomUUID())
                .email("test@test.com")
                .nombres("Test User")
                .documentoIdentidad("123456789")
                .monto(new java.math.BigDecimal("10000"))
                .plazoMeses(12)
                .tipoPrestamoId(java.util.UUID.randomUUID())
                .tipoPrestamo("Personal")
                .estado(Estado.PENDIENTE_REVISION)
                .created(java.time.Instant.now())
                .build();
        assertNotNull(solicitud);
        assertEquals("test@test.com", solicitud.getEmail());
        assertEquals("Test User", solicitud.getNombres());
        assertEquals("123456789", solicitud.getDocumentoIdentidad());
        assertEquals(new java.math.BigDecimal("10000"), solicitud.getMonto());
        assertEquals(12, solicitud.getPlazoMeses());
        assertEquals("Personal", solicitud.getTipoPrestamo());
        assertEquals(Estado.PENDIENTE_REVISION, solicitud.getEstado());
        assertNotNull(solicitud.getCreated());

    }

    @Test
    @DisplayName("Crear Getters y Setters - Exitoso")
    void testGettersAndSetters() {
        Solicitud solicitud = new Solicitud();
        java.util.UUID id = java.util.UUID.randomUUID();
        java.util.UUID tipoPrestamoId = java.util.UUID.randomUUID();
        java.time.Instant created = java.time.Instant.now();
        solicitud.setId(id);
        solicitud.setEmail("test@test.com");
        solicitud.setNombres("Test User");
        solicitud.setDocumentoIdentidad("123456789");
        solicitud.setMonto(new java.math.BigDecimal("10000"));
        solicitud.setPlazoMeses(12);
        solicitud.setTipoPrestamoId(tipoPrestamoId);
        solicitud.setTipoPrestamo("Personal");
        solicitud.setEstado(Estado.PENDIENTE_REVISION);
        solicitud.setCreated(created);

        assertEquals(id, solicitud.getId());
        assertEquals("test@test.com", solicitud.getEmail());
        assertEquals("Test User", solicitud.getNombres());
        assertEquals("123456789", solicitud.getDocumentoIdentidad());
        assertEquals(new java.math.BigDecimal("10000"), solicitud.getMonto());
        assertEquals(12, solicitud.getPlazoMeses());
        assertEquals(tipoPrestamoId, solicitud.getTipoPrestamoId());
        assertEquals("Personal", solicitud.getTipoPrestamo());
        assertEquals(Estado.PENDIENTE_REVISION, solicitud.getEstado());
        assertEquals(created, solicitud.getCreated());

    }

    @Test
    @DisplayName("Crear Solicitud sin parametros - Exitoso")
    void testCrearSolicitudSinParametros() {
        Solicitud solicitud = new Solicitud();
        assertNotNull(solicitud);
    }

    @Test
    @DisplayName("Crear Solicitud con todos los argumentos - Exitoso")
    void testCrearSolicitudConTodosLosArgumentos() {
        java.util.UUID id = java.util.UUID.randomUUID();
        java.util.UUID tipoPrestamoId = java.util.UUID.randomUUID();
        java.time.Instant created = java.time.Instant.now();
        Solicitud solicitud = new Solicitud(
                id, "test@test.com", "Test User", "123456789",
                new java.math.BigDecimal("10000"), 12, tipoPrestamoId,
                "Personal", Estado.PENDIENTE_REVISION, created, 15.5, 
                new java.math.BigDecimal("1000000"), null, null // eventId y prestamosActivos
        );
        assertNotNull(solicitud);
        assertEquals(id, solicitud.getId());
        assertEquals("test@test.com", solicitud.getEmail());
    }
}
