package co.com.crediya.solicitudes.api.dto;

import java.math.BigDecimal;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Representa la solicitud de entrada para crear una nueva solicitud de crédito.")
public record CrearSolicitudRequest(
    @Schema(description = "Correo electrónico del cliente que realiza la solicitud.", example = "juan.perez@example.com")
    String email,
    @Schema(description = "El monto de dinero solicitado. Debe ser un valor positivo.", example = "5000000")
    BigDecimal monto,
    @Schema(description = "El número de meses en los que se planea pagar el crédito. Debe ser un entero positivo.", example = "24")
    Integer plazo_meses,
    @Schema(description = "El nombre del tipo de préstamo que se está solicitando.", example = "LIBRE_INVERSION")
    String tipo_prestamo) {

}
