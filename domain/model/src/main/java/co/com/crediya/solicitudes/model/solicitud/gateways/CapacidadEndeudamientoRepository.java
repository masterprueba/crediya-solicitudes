package co.com.crediya.solicitudes.model.solicitud.gateways;

import co.com.crediya.solicitudes.model.solicitud.Solicitud;
import reactor.core.publisher.Mono;

public interface CapacidadEndeudamientoRepository {
    Mono<Void> validarCapacidadEndeudamiento(Solicitud solicitud);
}
