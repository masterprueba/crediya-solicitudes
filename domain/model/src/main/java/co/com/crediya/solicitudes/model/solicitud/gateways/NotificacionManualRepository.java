package co.com.crediya.solicitudes.model.solicitud.gateways;

import co.com.crediya.solicitudes.model.solicitud.Solicitud;
import reactor.core.publisher.Mono;

public interface NotificacionManualRepository {
    Mono<Void> enviarCorreoSolicitud(Solicitud solicitud);

}
