package co.com.crediya.solicitudes.sqs.listener;

import co.com.crediya.solicitudes.model.solicitud.DecisionSolicitud;
import co.com.crediya.solicitudes.model.solicitud.Estado;
import co.com.crediya.solicitudes.sqs.listener.dto.CapacidadDecisionRequest;
import co.com.crediya.solicitudes.usecase.cambiarestadosolicitud.CambiarEstadoSolicitudUseCase;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.services.sqs.model.Message;

import java.util.function.Function;

@Service
@RequiredArgsConstructor
@Log4j2
public class SQSProcessor implements Function<Message, Mono<Void>> {
    private final CambiarEstadoSolicitudUseCase cambiarEstadoSolicitudUseCase;
    private final ObjectMapper objectMapper;

    @Override
    public Mono<Void> apply(Message message) {
        return Mono.fromCallable(() -> objectMapper.readValue(message.body(), CapacidadDecisionRequest.class))
                .doOnSuccess(dto -> log.info("✅ JSON parseado correctamente - EventId: {}, SolicitudId: {}, Decision: {}", 
                        dto.eventId(), dto.solicitudId(), dto.decision()))
                .flatMap(this::procesarDecision)
                .doOnSuccess(ignored -> log.info("✅ Mensaje procesado y confirmado exitosamente"))
                .doOnError(e -> log.error("❌ Error procesando mensaje SQS - MessageId: {}, Error: {}", message.messageId(), e.getMessage(), e))
                .then();
    }

    private Mono<Void> procesarDecision(CapacidadDecisionRequest dto) {

        DecisionSolicitud decision = DecisionSolicitud.builder()
                .eventId(dto.eventId())
                .solicitudId(dto.solicitudId())
                .decision(Estado.valueOf(dto.decision()))
                .capacidadMax(dto.capacidadMax())
                .deudaMensualActual(dto.deudaMensualActual())
                .capacidadDisponible(dto.capacidadDisponible())
                .cuotaPrestamoNuevo(dto.cuotaPrestamoNuevo())
                .planPago(dto.planPago() == null ? null : dto.planPago().toString())
                .decidedAt(dto.decidedAt())
                .build();

        return cambiarEstadoSolicitudUseCase.cambiarEstadoReporte( decision,"AUTOMATICA")
                .doOnSuccess(ignored -> log.info("✅ Estado de solicitud cambiado exitosamente - SolicitudId: {}", dto.solicitudId()))
                .doOnError(e -> log.error("❌ Error cambiando estado de solicitud - SolicitudId: {}, Error: {}", dto.solicitudId(), e.getMessage(), e))
                .then();
    }
}
