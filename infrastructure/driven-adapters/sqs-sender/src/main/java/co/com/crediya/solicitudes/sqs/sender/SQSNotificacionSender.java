package co.com.crediya.solicitudes.sqs.sender;

import co.com.crediya.solicitudes.model.solicitud.Solicitud;
import co.com.crediya.solicitudes.model.solicitud.gateways.NotificacionRepository;
import co.com.crediya.solicitudes.model.solicitud.DecisionSolicitud;
import co.com.crediya.solicitudes.sqs.sender.config.SQSSenderProperties;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.services.sqs.SqsAsyncClient;
import java.util.Locale;

@Service
@Log4j2
public class SQSNotificacionSender extends SQSBaseSender implements NotificacionRepository {
    private final SQSSenderProperties properties;

    public SQSNotificacionSender(SqsAsyncClient client, SQSSenderProperties properties) {
        super(client);
        this.properties = properties;
    }

    @Override
    public Mono<Void> enviarDecisionSolicitud(Solicitud solicitud) {
        String mensaje = convertirSolicitudANotificacionJson(solicitud);
        return send(mensaje, properties.notificaciones().url())
                .doOnSuccess(messageId -> log.info("Notificación enviada para la solicitud {}: {}", 
                        solicitud.getId(), messageId))
                .doOnError(error -> log.error("Error al enviar notificación para solicitud {}: {}", 
                        solicitud.getId(), error.getMessage()))
                .then();
    }

    @Override
    public Mono<Void> enviarDecisionSolicitud(Solicitud solicitud, DecisionSolicitud decision) {
        String mensaje = convertirSolicitudYDecisionANotificacionJson(solicitud, decision);
        String dedupId = decision.getEventId() != null ? decision.getEventId().toString() : null;
        return send(mensaje, properties.notificaciones().url(), "solicitudes", dedupId)
                .doOnSuccess(messageId -> log.info("Notificación enviada para la solicitud {}: {}", 
                        solicitud.getId(), messageId))
                .doOnError(error -> log.error("Error al enviar notificación para solicitud {}: {}", 
                        solicitud.getId(), error.getMessage()))
                .then();
    }

    private String convertirSolicitudANotificacionJson(Solicitud solicitud) {
        return String.format(
                "{\"solicitudId\": \"%s\", \"nuevoEstado\": \"%s\", \"nombreSolicitante\": \"%s\", \"emailSolicitante\": \"%s\"}",
                solicitud.getId(), solicitud.getEstado(), solicitud.getNombres(), solicitud.getEmail()
        );
    }

    private String convertirSolicitudYDecisionANotificacionJson(Solicitud solicitud, DecisionSolicitud d) {
        String planPagoJson = d.getPlanPago() == null ? "null" : d.getPlanPago();
        return String.format(Locale.US,
                "{\"eventId\": \"%s\", \"solicitudId\": \"%s\", \"nuevoEstado\": \"%s\", \"nombreSolicitante\": \"%s\", \"emailSolicitante\": \"%s\", \"capacidadMax\": %s, \"deudaMensualActual\": %s, \"capacidadDisponible\": %s, \"cuotaPrestamoNuevo\": %s, \"planPago\": %s, \"decididoEn\": \"%s\", \"montoSolicitado\": %s}",
                d.getEventId(),
                solicitud.getId(),
                solicitud.getEstado(),
                solicitud.getNombres(),
                solicitud.getEmail(),
                toJsonNumber(d.getCapacidadMax()),
                toJsonNumber(d.getDeudaMensualActual()),
                toJsonNumber(d.getCapacidadDisponible()),
                toJsonNumber(d.getCuotaPrestamoNuevo()),
                planPagoJson,
                d.getDecidedAt(),
                toJsonNumber(solicitud.getMonto())
        );
    }

    private String toJsonStringOrNull(String value) {
        return value == null ? "null" : String.format("\"%s\"", value);
    }

    private Object toJsonNumber(Object value) {
        return value == null ? "null" : value;
    }
}
