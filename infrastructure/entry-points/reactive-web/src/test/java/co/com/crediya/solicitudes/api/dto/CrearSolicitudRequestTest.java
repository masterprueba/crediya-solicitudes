package co.com.crediya.solicitudes.api.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@DisplayName("CrearSolicitudRequest Test")
class CrearSolicitudRequestTest {

    @Test
    @DisplayName("Debe crear una instancia de CrearSolicitudRequest")
    void debeCrearInstanciaDeCrearSolicitudRequest() {
        CrearSolicitudRequest request = new CrearSolicitudRequest("test@test.com", new BigDecimal("1000.0"), 12, "Personal");
        assertNotNull(request);
    }

    @Test
    @DisplayName("Debe tener getters")
    void debeTenerGetters() {
        CrearSolicitudRequest request = new CrearSolicitudRequest("test@test.com", new BigDecimal("1000.0"), 12, "Personal");
        assertNotNull(request.email());
        assertNotNull(request.monto());
        assertNotNull(request.plazo_meses());
        assertNotNull(request.tipo_prestamo());
    }

}
