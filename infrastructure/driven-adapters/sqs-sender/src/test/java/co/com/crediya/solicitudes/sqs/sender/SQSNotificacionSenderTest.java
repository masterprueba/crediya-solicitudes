package co.com.crediya.solicitudes.sqs.sender;

import co.com.crediya.solicitudes.model.solicitud.Estado;
import co.com.crediya.solicitudes.model.solicitud.Solicitud;
import co.com.crediya.solicitudes.sqs.sender.config.SQSSenderProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.test.StepVerifier;
import software.amazon.awssdk.services.sqs.SqsAsyncClient;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;
import software.amazon.awssdk.services.sqs.model.SendMessageResponse;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("SQS Notificacion Sender Test")
class SQSNotificacionSenderTest {

    @Mock
    private SqsAsyncClient sqsAsyncClient;

    @Mock
    private SQSSenderProperties properties;

    @Mock
    private SQSSenderProperties.QueueConfig notificacionesConfig;

    private SQSNotificacionManualSender sender;

    @BeforeEach
    void setUp() {
        when(properties.notificaciones()).thenReturn(notificacionesConfig);
        when(notificacionesConfig.url()).thenReturn("https://sqs.us-east-2.amazonaws.com/123456789/test-queue");
        sender = new SQSNotificacionManualSender(sqsAsyncClient, properties);
    }

    @Test
    @DisplayName("Enviar notificación de decisión de solicitud exitosamente")
    void enviarDecisionSolicitudExitoso() {
        // Given
        Solicitud solicitud = crearSolicitudMock();
        SendMessageResponse response = SendMessageResponse.builder()
                .messageId("test-message-id")
                .build();

        when(sqsAsyncClient.sendMessage(any(SendMessageRequest.class)))
                .thenReturn(CompletableFuture.completedFuture(response));

        // When & Then
        StepVerifier.create(sender.enviarCorreoSolicitud(solicitud))
                .verifyComplete();
    }

    @Test
    @DisplayName("Manejar error al enviar notificación")
    void enviarDecisionSolicitudError() {
        // Given
        Solicitud solicitud = crearSolicitudMock();
        RuntimeException error = new RuntimeException("SQS Error");

        when(sqsAsyncClient.sendMessage(any(SendMessageRequest.class)))
                .thenReturn(CompletableFuture.failedFuture(error));

        // When & Then
        StepVerifier.create(sender.enviarCorreoSolicitud(solicitud))
                .expectError(RuntimeException.class)
                .verify();
    }

    private Solicitud crearSolicitudMock() {
        return Solicitud.builder()
                .id(UUID.randomUUID())
                .email("test@test.com")
                .nombres("Test User")
                .documentoIdentidad("123456789")
                .monto(new BigDecimal("50000"))
                .plazoMeses(24)
                .tipoPrestamoId(UUID.randomUUID())
                .tipoPrestamo("Personal")
                .estado(Estado.APROBADA)
                .created(Instant.now())
                .build();
    }
}
