package co.com.crediya.solicitudes.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

@Schema(description = "Representa la respuesta después de cambiar el estado de una solicitud de crédito.")
public record CambiarEstadoResponse(
    @Schema(description = "El identificador único de la solicitud de crédito cuyo estado ha sido cambiado.", example = "123e4567-e89b-12d3-a456-426614174000")
    String solicitudId,
    @Schema(description = "El nuevo estado de la solicitud de crédito después del cambio.", example = "APROBADA o RECHAZADA")
    String nuevoEstado,
    @Schema(description = "El monto aprobado para la solicitud de crédito. Este campo es relevante si el nuevo estado es 'APROBADA'.", example = "5000000")
    BigDecimal monto,
    @Schema(description = "El correo electrónico del cliente que realizó la solicitud de crédito.", example = "teste@example.com")
    String emailCliente
) {
}
