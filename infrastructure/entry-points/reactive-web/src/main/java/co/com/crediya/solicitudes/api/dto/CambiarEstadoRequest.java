package co.com.crediya.solicitudes.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Representa la solicitud de entrada para cambiar el estado de una solicitud de crédito.")
public record CambiarEstadoRequest(
    @Schema(description = "El nuevo estado de la solicitud de crédito. Debe ser uno de los estados válidos definidos en el sistema.", example = "APROBADA o RECHAZADA")
    String estado
) {
}
