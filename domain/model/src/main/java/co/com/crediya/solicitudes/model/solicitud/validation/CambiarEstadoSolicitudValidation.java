package co.com.crediya.solicitudes.model.solicitud.validation;

import co.com.crediya.solicitudes.model.solicitud.Solicitud;
import reactor.core.publisher.Mono;

@FunctionalInterface
public interface CambiarEstadoSolicitudValidation {
    Mono<String> validar(Solicitud solicitud, String estado);

    default CambiarEstadoSolicitudValidation and(CambiarEstadoSolicitudValidation other) {
        return (solicitud, estado) -> this.validar(solicitud, estado)
                .flatMap(estado1 -> other.validar(solicitud, estado1));
    }
}
