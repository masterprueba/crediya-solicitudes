package co.com.crediya.solicitudes.model.solicitud.validation;

import co.com.crediya.solicitudes.model.solicitud.Solicitud;
import reactor.core.publisher.Mono;

@FunctionalInterface
public interface SolicitudValidation {
    Mono<Solicitud> validar(Solicitud solicitud);

    default SolicitudValidation and(SolicitudValidation other) {
        return solicitud -> this.validar(solicitud)
        .flatMap(other::validar);
    }
}
