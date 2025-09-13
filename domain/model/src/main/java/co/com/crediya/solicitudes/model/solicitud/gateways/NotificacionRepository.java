package co.com.crediya.solicitudes.model.solicitud.gateways;

import co.com.crediya.solicitudes.model.solicitud.Solicitud;
import co.com.crediya.solicitudes.model.solicitud.DecisionSolicitud;
import reactor.core.publisher.Mono;

public interface NotificacionRepository {
    Mono<Void> enviarDecisionSolicitud(Solicitud solicitud);
    Mono<Void> enviarDecisionSolicitud(Solicitud solicitud, DecisionSolicitud decision);
}
