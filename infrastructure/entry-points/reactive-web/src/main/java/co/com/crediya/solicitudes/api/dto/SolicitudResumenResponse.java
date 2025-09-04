package co.com.crediya.solicitudes.api.dto;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record SolicitudResumenResponse(
        List<SolicitudItem> contenido,
        int page, int size, long totalElements, int totalPages, boolean hasNext
) {
    public record SolicitudItem(
            UUID id, String documento_cliente, BigDecimal monto, Integer plazo_meses,
            String tipo_prestamo, BigDecimal tasa_interes, String estado,
            String nombre, String email, BigDecimal salario_base, BigDecimal deuda_total_mensual_aprobadas
    ) {}
}
