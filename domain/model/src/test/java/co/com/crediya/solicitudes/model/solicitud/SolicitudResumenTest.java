package co.com.crediya.solicitudes.model.solicitud;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class SolicitudResumenTest {

    @Test
    void crearSolicitudResumen_y_verificarCampos() {
        UUID id = UUID.randomUUID();
        String documentoCliente = "123456";
        BigDecimal monto = BigDecimal.valueOf(5000);
        Integer plazoMeses = 12;
        String tipoPrestamo = "PERSONAL";
        BigDecimal tasaInteres = BigDecimal.valueOf(8.5);
        Estado estado = Estado.PENDIENTE_REVISION;
        String nombreCompleto = "Juan Pérez";
        String email = "juan@test.com";
        BigDecimal salarioBase = BigDecimal.valueOf(2000);
        BigDecimal deudaTotalMensualAprobadas = BigDecimal.valueOf(300);

        SolicitudResumen resumen = new SolicitudResumen(
                id, documentoCliente, monto, plazoMeses, tipoPrestamo, tasaInteres,
                estado, nombreCompleto, email, salarioBase, deudaTotalMensualAprobadas
        );

        assertEquals(id, resumen.getId());
        assertEquals(documentoCliente, resumen.getDocumentoCliente());
        assertEquals(monto, resumen.getMonto());
        assertEquals(plazoMeses, resumen.getPlazoMeses());
        assertEquals(tipoPrestamo, resumen.getTipoPrestamo());
        assertEquals(tasaInteres, resumen.getTasaInteres());
        assertEquals(estado, resumen.getEstado());
        assertEquals(nombreCompleto, resumen.getNombreCompleto());
        assertEquals(email, resumen.getEmail());
        assertEquals(salarioBase, resumen.getSalarioBase());
        assertEquals(deudaTotalMensualAprobadas, resumen.getDeudaTotalMensualAprobadas());
    }
}
