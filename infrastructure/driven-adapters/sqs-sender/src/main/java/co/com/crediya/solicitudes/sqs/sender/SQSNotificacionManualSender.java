package co.com.crediya.solicitudes.sqs.sender;

import co.com.crediya.solicitudes.model.exceptions.DomainException;
import co.com.crediya.solicitudes.model.solicitud.Solicitud;
import co.com.crediya.solicitudes.model.solicitud.gateways.NotificacionManualRepository;
import co.com.crediya.solicitudes.model.solicitud.DecisionSolicitud;
import co.com.crediya.solicitudes.sqs.sender.config.SQSSenderProperties;
import co.com.crediya.solicitudes.sqs.sender.helper.CorreoUtils;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.services.sqs.SqsAsyncClient;
import java.util.Locale;

@Service
@Log4j2
public class SQSNotificacionManualSender extends SQSBaseSender implements NotificacionManualRepository {
    private final SQSSenderProperties properties;


    public SQSNotificacionManualSender(SqsAsyncClient client, SQSSenderProperties properties) {
        super(client);
        this.properties = properties;
    }

    @Override
    public Mono<Void> enviarCorreoSolicitud(Solicitud solicitud) {
        if (solicitud.getEmail() == null || solicitud.getEstado() == null) {
            return Mono.error(new DomainException("Datos de la solicitud incompletos"));
        }

        String asunto = "Resultado de tu evaluación de crédito";
        String cuerpo = CorreoUtils.construirCuerpoManual(solicitud);
        String mensaje = CorreoUtils.construirCorreo(asunto, cuerpo, solicitud);

        return send(mensaje, properties.notificaciones().url())
                .doOnSuccess(messageId -> log.info("Notificación enviada para la solicitud {}: {}", solicitud.getId(), messageId))
                .doOnError(error -> log.error("Error al enviar notificación para solicitud {}: {}", solicitud.getId(), error.getMessage()))
                .onErrorResume(error -> {
                    log.warn("Recuperando de error: {}", error.getMessage());
                    return Mono.empty();
                })
                .then();
    }

}
