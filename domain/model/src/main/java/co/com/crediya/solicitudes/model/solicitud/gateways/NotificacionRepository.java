package co.com.crediya.solicitudes.model.solicitud.gateways;

import co.com.crediya.solicitudes.model.solicitud.Solicitud;
import reactor.core.publisher.Mono;

public interface NotificacionRepository {
    Mono<Void> enviarDecisionSolicitud(Solicitud solicitud);
}
