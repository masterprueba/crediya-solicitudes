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
import reactor.core.publisher.Mono;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class CrearSolicitudHandler  {
    private final CrearSolicitudUseCase useCase;
    private final Logger log = LoggerFactory.getLogger(CrearSolicitudHandler.class);

  public Mono<ServerResponse> crear(ServerRequest req) {
    return req.bodyToMono(CrearSolicitudRequest.class)
      .doOnNext(dto -> log.info("crear-solicitud intento email={}", dto.email()))
      .map(this::toDomain)
      .flatMap(useCase::ejecutar)
      .flatMap(sol -> {
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
