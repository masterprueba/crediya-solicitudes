package co.com.crediya.solicitudes.model.solicitud;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DisplayName("Solicitud Test")
class SolicitudTest {

    @Test
    @DisplayName("Probar toBuilder y campos opcionales")
    void testToBuilderYCamposOpcionales() {
        UUID id = UUID.randomUUID();
        UUID tipoPrestamoId = UUID.randomUUID();
        UUID eventId = UUID.randomUUID();
        Instant created = Instant.now();
        PrestamoActivo prestamoActivo = new PrestamoActivo(); // Asegúrate de tener un constructor vacío
        List<PrestamoActivo> prestamosActivos = List.of(prestamoActivo);

        Solicitud solicitud = Solicitud.builder()
                .id(id)
                .email("test2@test.com")
                .nombres("Otro User")
                .documentoIdentidad("987654321")
                .monto(new BigDecimal("5000"))
                .plazoMeses(6)
                .tipoPrestamoId(tipoPrestamoId)
                .tipoPrestamo("Vehículo")
                .estado(Estado.APROBADA)
                .created(created)
                .tasaInteres(10.5)
                .salarioBase(new BigDecimal("2000000"))
                .eventId(eventId)
                .prestamosActivos(prestamosActivos)
                .build();

        Solicitud copia = solicitud.toBuilder().email("nuevo@test.com").build();

        assertEquals("test2@test.com", solicitud.getEmail());
        assertEquals("nuevo@test.com", copia.getEmail());
        assertEquals("Otro User", copia.getNombres());
        assertEquals("987654321", copia.getDocumentoIdentidad());
        assertEquals(new BigDecimal("5000"), copia.getMonto());
        assertEquals(6, copia.getPlazoMeses());
        assertEquals(tipoPrestamoId, copia.getTipoPrestamoId());
        assertEquals("Vehículo", copia.getTipoPrestamo());
        assertEquals(Estado.APROBADA, copia.getEstado());
        assertEquals(created, copia.getCreated());
        assertEquals(eventId, solicitud.getEventId());
        assertEquals(prestamosActivos, solicitud.getPrestamosActivos());
        assertEquals(10.5, solicitud.getTasaInteres());
        assertEquals(new BigDecimal("2000000"), solicitud.getSalarioBase());
        assertNotNull(solicitud.getId());
        assertNotNull(solicitud.getCreated());
    }


}
