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
@DisplayName("SQS Capacidad Endeudamiento Sender Test")
class SQSCapacidadEndeudamientoSenderTest {

    @Mock
    private SqsAsyncClient sqsAsyncClient;

    @Mock
    private SQSSenderProperties properties;

    @Mock
    private SQSSenderProperties.QueueConfig capacidadEndeudamientoConfig;

    private SQSCapacidadEndeudamientoSender sender;

    @BeforeEach
    void setUp() {
        when(properties.capacidadEndeudamiento()).thenReturn(capacidadEndeudamientoConfig);
        when(capacidadEndeudamientoConfig.url()).thenReturn("https://sqs.us-east-2.amazonaws.com/123456789/capacidad-queue");
        sender = new SQSCapacidadEndeudamientoSender(sqsAsyncClient, properties);
    }

    @Test
    @DisplayName("Validar capacidad de endeudamiento exitosamente")
    void validarCapacidadEndeudamientoExitoso() {
        // Given
        Solicitud solicitud = crearSolicitudMock();
        SendMessageResponse response = SendMessageResponse.builder()
                .messageId("test-message-id")
                .build();

        when(sqsAsyncClient.sendMessage(any(SendMessageRequest.class)))
                .thenReturn(CompletableFuture.completedFuture(response));

        // When & Then
        StepVerifier.create(sender.validarCapacidadEndeudamiento(solicitud))
                .verifyComplete();
    }

    @Test
    @DisplayName("Manejar error al validar capacidad de endeudamiento")
    void validarCapacidadEndeudamientoError() {
        // Given
        Solicitud solicitud = crearSolicitudMock();
        RuntimeException error = new RuntimeException("SQS Error");

        when(sqsAsyncClient.sendMessage(any(SendMessageRequest.class)))
                .thenReturn(CompletableFuture.failedFuture(error));

        // When & Then
        StepVerifier.create(sender.validarCapacidadEndeudamiento(solicitud))
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
                .estado(Estado.EN_VALIDACION_AUTOMATICA)
                .created(Instant.now())
                .build();
    }
}
