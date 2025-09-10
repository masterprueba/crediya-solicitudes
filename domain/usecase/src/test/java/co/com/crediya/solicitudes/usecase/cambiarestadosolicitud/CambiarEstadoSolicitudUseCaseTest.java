package co.com.crediya.solicitudes.usecase.cambiarestadosolicitud;

import co.com.crediya.solicitudes.model.exceptions.DomainException;
import co.com.crediya.solicitudes.model.solicitud.Estado;
import co.com.crediya.solicitudes.model.solicitud.Solicitud;
import co.com.crediya.solicitudes.model.solicitud.gateways.NotificacionRepository;
import co.com.crediya.solicitudes.model.solicitud.gateways.SolicitudRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class CambiarEstadoSolicitudUseCaseTest {

    private SolicitudRepository solicitudRepository;
    private NotificacionRepository notificacionRepository;
    private CambiarEstadoSolicitudUseCase useCase;

    @BeforeEach
    void setUp() {
        solicitudRepository = mock(SolicitudRepository.class);
        notificacionRepository = mock(NotificacionRepository.class);
        useCase = new CambiarEstadoSolicitudUseCase(solicitudRepository, notificacionRepository);
    }

    @Test
    void cambiarEstado_exito() {
        String solicitudId = UUID.randomUUID().toString();
        String nuevoEstado = "APROBADA";
        Solicitud solicitud = new Solicitud();
        solicitud.setId(UUID.fromString(solicitudId));
        solicitud.setEstado(Estado.PENDIENTE_REVISION);

        when(solicitudRepository.findById(solicitudId)).thenReturn(Mono.just(solicitud));
        when(solicitudRepository.update(any(Solicitud.class))).thenReturn(Mono.just(solicitud));
        when(notificacionRepository.enviarDecisionSolicitud(any(Solicitud.class))).thenReturn(Mono.empty());

        StepVerifier.create(useCase.cambiarEstado(solicitudId, nuevoEstado))
                .expectNextMatches(s -> s.getEstado() == Estado.APROBADA)
                .verifyComplete();

        verify(solicitudRepository).findById(solicitudId);
        verify(solicitudRepository).update(any(Solicitud.class));
        verify(notificacionRepository).enviarDecisionSolicitud(any(Solicitud.class));
    }

    @Test
    void cambiarEstado_solicitudNoEncontrada() {
        String solicitudId = "2";
        String nuevoEstado = "RECHAZADA";

        when(solicitudRepository.findById(solicitudId)).thenReturn(Mono.empty());

        StepVerifier.create(useCase.cambiarEstado(solicitudId, nuevoEstado))
                .expectError(DomainException.class)
                .verify();

        verify(solicitudRepository).findById(solicitudId);
        verifyNoMoreInteractions(solicitudRepository, notificacionRepository);
    }
}