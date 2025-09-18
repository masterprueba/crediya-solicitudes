package co.com.crediya.solicitudes.sqs.sender;

import co.com.crediya.solicitudes.model.exceptions.DomainException;
import co.com.crediya.solicitudes.model.solicitud.DecisionSolicitud;
import co.com.crediya.solicitudes.model.solicitud.Estado;
import co.com.crediya.solicitudes.model.solicitud.Solicitud;
import co.com.crediya.solicitudes.model.solicitud.gateways.NotificacionAutomaticaRepository;
import co.com.crediya.solicitudes.model.solicitud.gateways.NotificacionManualRepository;
import co.com.crediya.solicitudes.sqs.sender.config.SQSSenderProperties;
import co.com.crediya.solicitudes.sqs.sender.helper.CorreoUtils;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.services.sqs.SqsAsyncClient;

import java.math.BigDecimal;
import java.util.Locale;

@Service
@Log4j2
public class SQSNotificacionAutomaticoSender extends SQSBaseSender implements NotificacionAutomaticaRepository {
    private final SQSSenderProperties properties;


    public SQSNotificacionAutomaticoSender(SqsAsyncClient client, SQSSenderProperties properties) {
        super(client);
        this.properties = properties;
    }

    @Override
    public Mono<Void> enviarCorreoSolicitud(DecisionSolicitud decision, Solicitud solicitud) {
        if (solicitud.getEmail() == null) {
            return Mono.error(new DomainException("Datos de la solicitud incompletos"));
        }

        String asunto = "Resultado de tu evaluación de crédito";

        if (Estado.APROBADA.equals(solicitud.getEstado())) {
            return CorreoUtils.construirCuerpoAutomaticoAprobadoReactivo(decision, solicitud.getMonto())
                    .flatMap(cuerpo -> {
                        String mensaje = CorreoUtils.construirCorreo(asunto, cuerpo, solicitud);
                        return send(mensaje, properties.notificaciones().url());
                    })
                    .doOnSuccess(messageId -> log.info("Notificación enviada para la solicitud {}: {}", solicitud.getId(), messageId))
                    .doOnError(error -> log.error("Error al enviar notificación para solicitud {}: {}", solicitud.getId(), error.getMessage()))
                    .onErrorResume(error -> {
                        log.warn("Recuperando de error: {}", error.getMessage());
                        return Mono.empty();
                    })
                    .then();
        } else {
            return CorreoUtils.construirCuerpoAutomaticoRechazadaReactivo(decision)
                    .flatMap(cuerpo -> {
                        String mensaje = CorreoUtils.construirCorreo(asunto, cuerpo, solicitud);
                        return send(mensaje, properties.notificaciones().url());
                    })
                    .doOnSuccess(messageId -> log.info("Notificación enviada para la solicitud {}: {}", solicitud.getId(), messageId))
                    .doOnError(error -> log.error("Error al enviar notificación para solicitud {}: {}", solicitud.getId(), error.getMessage()))
                    .onErrorResume(error -> {
                        log.warn("Recuperando de error: {}", error.getMessage());
                        return Mono.empty();
                    })
                    .then();
        }
    }

}
