package co.com.crediya.solicitudes.sqs.sender;

import co.com.crediya.solicitudes.model.solicitud.Solicitud;
import co.com.crediya.solicitudes.model.solicitud.gateways.NotificacionRepository;
import co.com.crediya.solicitudes.sqs.sender.config.SQSSenderProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.services.sqs.SqsAsyncClient;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;
import software.amazon.awssdk.services.sqs.model.SendMessageResponse;

@Service
@Log4j2
@RequiredArgsConstructor
public class SQSSender implements NotificacionRepository {
    private final SQSSenderProperties properties;
    private final SqsAsyncClient client;

    public Mono<String> send(String message) {
        return Mono.fromCallable(() -> buildRequest(message))
                .flatMap(request -> Mono.fromFuture(client.sendMessage(request)))
                .doOnNext(response -> log.debug("Message sent {}", response.messageId()))
                .map(SendMessageResponse::messageId);
    }

    private SendMessageRequest buildRequest(String message) {
        return SendMessageRequest.builder()
                .queueUrl(properties.queueUrl())
                .messageBody(message)
                .build();
    }

    @Override
    public Mono<Void> enviarDecisionSolicitud(Solicitud solicitud) {
        String mensaje = convertirSolicitudAJson(solicitud);
        return send(mensaje)
                .doOnSuccess(messageId -> log.info("Notificación enviada para la solicitud {}: {}", solicitud.getId(), messageId))
                .then();
    }

    private String convertirSolicitudAJson(Solicitud solicitud) {

        return String.format(
                "{\"solicitudId\": \"%s\", \"nuevoEstado\": \"%s\", \"nombreSolicitante\": \"%s\", \"emailSolicitante\": \"%s\"}",
                solicitud.getId(), solicitud.getEstado(), solicitud.getNombres(), solicitud.getEmail()
        );
    }
}
