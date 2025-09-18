package co.com.crediya.solicitudes.sqs.sender;

import co.com.crediya.solicitudes.model.solicitud.DecisionSolicitud;
import co.com.crediya.solicitudes.model.solicitud.Solicitud;
import co.com.crediya.solicitudes.model.solicitud.gateways.ReporteCambioEstadoRepository;
import co.com.crediya.solicitudes.sqs.sender.config.SQSSenderProperties;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.services.sqs.SqsAsyncClient;

import java.util.Locale;

@Service
@Log4j2
public class SQSEstadoSolicitudSender extends SQSBaseSender implements ReporteCambioEstadoRepository {
    private final SQSSenderProperties properties;

    public SQSEstadoSolicitudSender(SqsAsyncClient client, SQSSenderProperties properties) {
        super(client);
        this.properties = properties;
    }

    @Override
    public Mono<Void> enviarReporteSolicitud(DecisionSolicitud decision, Solicitud solicitud) {

        String dedupId = decision.getEventId() != null ? decision.getEventId().toString() : solicitud.getId().toString();
        String mensaje = convertirSolicitudANotificacionJson(solicitud, dedupId);
        return send(mensaje, properties.reporteCambiosEstado().url(), "Evento", dedupId)
                .doOnSuccess(messageId -> log.info("Notificación enviada para la solicitud {}: {}",
                        solicitud.getId(), messageId))
                .doOnError(error -> log.error("Error al enviar notificación para solicitud {}: {}",
                        solicitud.getId(), error.getMessage()))
                .then();
    }

    private String convertirSolicitudANotificacionJson(Solicitud solicitud, String id) {
        return String.format(
                "{\"idEvento\": \"%s\", \"idSolicitud\": \"%s\", \"fechaDecidido\": \"%s\", \"montoAprobado\": \"%s\"}",
                id, solicitud.getId(), solicitud.getCreated(), solicitud.getMonto()
        );
    }

}
