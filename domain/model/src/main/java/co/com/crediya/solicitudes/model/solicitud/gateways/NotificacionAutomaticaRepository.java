package co.com.crediya.solicitudes.model.solicitud.gateways;

import co.com.crediya.solicitudes.model.solicitud.DecisionSolicitud;
import co.com.crediya.solicitudes.model.solicitud.Solicitud;
import reactor.core.publisher.Mono;

public interface NotificacionAutomaticaRepository {
    Mono<Void> enviarCorreoSolicitud(DecisionSolicitud decisionSolicitud, Solicitud solicitud);

}
