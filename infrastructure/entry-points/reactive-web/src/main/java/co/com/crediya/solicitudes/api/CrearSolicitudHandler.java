package co.com.crediya.solicitudes.api;

import java.time.Instant;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import co.com.crediya.solicitudes.api.dto.CrearSolicitudRequest;
import co.com.crediya.solicitudes.model.solicitud.Estado;
import co.com.crediya.solicitudes.model.solicitud.Solicitud;
import co.com.crediya.solicitudes.usecase.crearsolicitud.CrearSolicitudUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.media.Content;
import reactor.core.publisher.Mono;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
@Tag(name = "Solicitud", description = "Operaciones relacionadas con las solicitudes de crédito")
public class CrearSolicitudHandler  {
    private final CrearSolicitudUseCase useCase;
    private final Logger log = LoggerFactory.getLogger(CrearSolicitudHandler.class);

    @Operation(
          summary = "Registrar una nueva solicitud de crédito",
          description = "Crea una nueva solicitud de crédito con la información proporcionada. " +
                      "Valida que todos los campos sean obligatorios, el monto sea mayor a 0, " +
                      "el plazo sea mayor a 0 y el tipo de prestamo sea valido.",
          operationId = "crearSolicitud"
    )
    @RequestBody(
          required = true,
          description = "Datos de la solicitud de crédito",
          content = @Content(
                  mediaType = "application/json",
                  schema = @Schema(implementation = CrearSolicitudRequest.class)
          )
    )
    @ApiResponses(value = {
          @ApiResponse(
                  responseCode = "201",
                  description = "Solicitud de crédito creada exitosamente"
          ),
          @ApiResponse(
                  responseCode = "400",
                  description = "Datos de entrada inválidos. Puede ser por: campos vacíos, " +
                              "monto menor a 0, plazo menor a 0 o tipo de prestamo no valido.",
                  content = @Content(
                          mediaType = "application/json",
                          schema = @Schema(example = """
                                  {
                                    "status": 400,
                                    "codigo": "DATOS_INVALIDOS",
                                    "mensaje": "La información proporcionada es inválida. El monto es menor a 0, el plazo es menor a 0 o el tipo de prestamo no es valido",
                                    "ruta": "/api/v1/solicitud"
                                  }
                                  """)
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
                                    "ruta": "/api/v1/solicitud"
                                  }
                                  """)
                  )
          )
    })
  public Mono<ServerResponse> crear(ServerRequest req) {
    return req.bodyToMono(CrearSolicitudRequest.class)
      .doOnNext(dto -> log.info("1. endpoint:{path:{}} parametro de entrada {}", req.path(), dto))
      .map(this::toDomain)
      .flatMap(useCase::ejecutar)
      .flatMap(sol -> {
        log.info("4. crear respuesta: {}", sol);
        var location = req.uriBuilder().path("/api/v1/solicitud/{id}").build(sol.getId());
        return ServerResponse.created(location).bodyValue(sol);
      });
  }

  private Solicitud toDomain(CrearSolicitudRequest r) {
    return Solicitud.builder()
    .email(r.email())
    .monto(r.monto())
    .plazoMeses(r.plazo_meses())
    .tipoPrestamo(r.tipo_prestamo())
    .estado(Estado.PENDIENTE_REVISION)
    .created(Instant.now())
    .build();
  }
}
