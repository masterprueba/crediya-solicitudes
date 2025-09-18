package co.com.crediya.solicitudes.api;

import co.com.crediya.solicitudes.api.dto.CambiarEstadoRequest;
import co.com.crediya.solicitudes.api.dto.CambiarEstadoResponse;
import co.com.crediya.solicitudes.api.mapper.CambiarEstadoMapper;
import co.com.crediya.solicitudes.model.solicitud.DecisionSolicitud;
import co.com.crediya.solicitudes.model.solicitud.Estado;
import co.com.crediya.solicitudes.usecase.cambiarestadosolicitud.CambiarEstadoSolicitudUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.UUID;


@Component
@RequiredArgsConstructor
@Tag(name = "Solicitud", description = "Operaciones relacionadas con las solicitudes de crédito")
public class CambiarEstadoSolicitudHandler {

    private final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(CambiarEstadoSolicitudHandler.class);

    private final CambiarEstadoSolicitudUseCase cambiarEstadoSolicitudUseCase;
    private final CambiarEstadoMapper cambiarEstadoMapper;


    @Operation(
            summary = "Cambiar el estado de una solicitud de crédito",
            description = "Permite cambiar el estado de una solicitud de crédito existente. " +
                    "El nuevo estado debe ser proporcionado como un parámetro de consulta.",
            operationId = "cambiarEstadoSolicitud"
    )
    @RequestBody(
            required = true,
            description = "Nuevo estado para la solicitud de crédito",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = CambiarEstadoRequest.class)
            )
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Estado de la solicitud cambiado exitosamente",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CambiarEstadoResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Solicitud inválida. Puede ser por: ID de solicitud no válido o estado no válido.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(example = """
                    {
                      "status": 400,
                      "codigo": "SOLICITUD_INVALIDA",
                      "mensaje": "El estado proporcionado no es válido. Valores permitidos: APROBADA, RECHAZADA."
                    }
                    """)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Solicitud no encontrada con el ID proporcionado.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(example = """
                    {
                      "status": 404,
                      "codigo": "NO_ENCONTRADO",
                      "mensaje": "Solicitud no encontrada con ID: {id}"
                    }
                    """)
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "500",
                    description = "Error interno del servidor",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(example = """
                    {
                      "status": 500,
                      "codigo": "ERROR_INTERNO",
                      "mensaje": "Ha ocurrido un error inesperado en el sistema. Por favor, contacte a soporte."
                    }
                    """)
                    )
            )
    })

    public Mono<ServerResponse> cambiarEstado(ServerRequest request) {
        String solicitudId = request.pathVariable("id");
        return request.bodyToMono(CambiarEstadoRequest.class)
                .doOnNext(dto -> log.info("Cambiar estado de solicitud {}", solicitudId))
                .flatMap(dto -> {
                    DecisionSolicitud decisionSolicitud = DecisionSolicitud.builder()
                            .decision(Estado.valueOf(dto.estado()))
                            .solicitudId(UUID.fromString(solicitudId))
                            .build();
                    return cambiarEstadoSolicitudUseCase.cambiarEstadoReporte(decisionSolicitud, "MANUAL")
                            .map(cambiarEstadoMapper::toResponse)
                            .flatMap(solicitud -> ServerResponse.ok().bodyValue(solicitud));
                });
    }
}
