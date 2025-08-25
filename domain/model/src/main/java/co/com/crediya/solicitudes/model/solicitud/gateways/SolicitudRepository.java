package co.com.crediya.solicitudes.model.solicitud.gateways;

import co.com.crediya.solicitudes.model.solicitud.Solicitud;
import reactor.core.publisher.Mono;

public interface SolicitudRepository {

    Mono<Solicitud> save(Solicitud s);
}
