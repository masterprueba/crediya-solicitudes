package co.com.crediya.solicitudes.api;

import co.com.crediya.solicitudes.api.dto.SolicitudResumenResponse;
import co.com.crediya.solicitudes.model.auth.AuthenticatedUser;
import co.com.crediya.solicitudes.model.cliente.ClienteToken;
import co.com.crediya.solicitudes.model.solicitud.SolicitudResumen;
import co.com.crediya.solicitudes.usecase.listarsolicitudes.ListarSolicitudesUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import static java.lang.Integer.parseInt;

@Slf4j
@Component
@RequiredArgsConstructor
@Schema(
        allOf = {CrearSolicitudHandler.class}
)
@Tag(name = "Solicitud", description = "Operaciones relacionadas con las solicitudes de crédito")
public class ListarSolicitudesHandler {

    private final ListarSolicitudesUseCase listarSolicitudesUseCase;

    @Operation(
            summary = "Listar solicitudes de crédito",
            description = "Obtiene una lista paginada de solicitudes de crédito filtradas por tipo y estado. " +
                         "IMPORTANTE: Solo usuarios con rol ASESOR pueden acceder a este endpoint.",
            operationId = "listarSolicitudes"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Listado de solicitudes obtenido exitosamente",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = SolicitudResumenResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Error interno del servidor",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(example = """
                    {
                      "status": 500,
                      "codigo": "ERROR_INTERNO",
                      "mensaje": "Ha ocurrido un error inesperado en el sistema. Por favor, contacte a soporte.",
                      "ruta": "/api/v1/solicitud/listar"
                    }
                    """)
                    )
            )
    })
    public Mono<ServerResponse> listarSolicitudes(ServerRequest req) {

        int page = parseInt(req.queryParam("page").orElse("0"), 10);
        int size = parseInt(req.queryParam("size").orElse("20"), 10);
        String tipo = req.queryParam("tipo").orElse(null);

        log.info("Listar solicitudes - page: {}, size: {}", page, size);

        return req.principal()
                .map(principal -> (AuthenticatedUser) ((Authentication) principal).getPrincipal())
                .flatMap(user -> {
                    ClienteToken clienteToken = ClienteToken.builder()
                            .email(user.getEmail())
                            .token(user.getToken())
                            .build();
                    return listarSolicitudesUseCase.listarSolicitudes(page, size, tipo, clienteToken);
                })
                .map(solicitudes -> new SolicitudResumenResponse(solicitudes.contenido().stream().map(this::toItem).toList(),
                        solicitudes.page(), solicitudes.size(), solicitudes.totalElements(), solicitudes.totalPages(), solicitudes.hasNext()))
                .flatMap(response -> ServerResponse.ok().bodyValue(response))
                .doOnError(e -> log.error("Error al listar solicitudes {}", e.getMessage()));

    }

    private SolicitudResumenResponse.SolicitudItem toItem(SolicitudResumen solicitudResumen) {
        return new SolicitudResumenResponse.SolicitudItem(
                solicitudResumen.getId(), solicitudResumen.getDocumentoCliente(),
                solicitudResumen.getMonto(), solicitudResumen.getPlazoMeses(), solicitudResumen.getTipoPrestamo(), solicitudResumen.getTasaInteres(),
                solicitudResumen.getEstado().name(), solicitudResumen.getNombreCompleto(), solicitudResumen.getEmail(),
                solicitudResumen.getSalarioBase(), solicitudResumen.getDeudaTotalMensualAprobadas()
        );
    }
}
