package co.com.crediya.solicitudes.model.solicitud.gateways;

import co.com.crediya.solicitudes.model.solicitud.Solicitud;
import reactor.core.publisher.Mono;

public interface SolicitudRepository {
    Mono<Solicitud> findById(String id);
    Mono<Solicitud> save(Solicitud solicitud);
    Mono<Solicitud> update(Solicitud solicitud);
}
