package co.com.crediya.solicitudes.usecase.cambiarestadosolicitud;

import co.com.crediya.solicitudes.model.exceptions.DomainException;
import co.com.crediya.solicitudes.model.solicitud.DecisionSolicitud;
import co.com.crediya.solicitudes.model.solicitud.Estado;
import co.com.crediya.solicitudes.model.solicitud.Solicitud;
import co.com.crediya.solicitudes.model.solicitud.gateways.NotificacionAutomaticaRepository;
import co.com.crediya.solicitudes.model.solicitud.gateways.NotificacionManualRepository;
import co.com.crediya.solicitudes.model.solicitud.gateways.ReporteCambioEstadoRepository;
import co.com.crediya.solicitudes.model.solicitud.gateways.SolicitudRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@DisplayName("CambiarEstadoSolicitudUseCase Test")
class CambiarEstadoSolicitudUseCaseTest {

    @Mock
    private SolicitudRepository solicitudRepository;
    @Mock
    private NotificacionManualRepository notificacionRepository;

    @Mock
    private NotificacionAutomaticaRepository notificacionAutomaticaRepository;

    @Mock
    private ReporteCambioEstadoRepository reporteCambioEstadoRepository;

    @InjectMocks
    private CambiarEstadoSolicitudUseCase cambiarEstadoSolicitudUseCase;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Cambiar estado de solicitud - Exitoso")
    void cambiarEstadoSolicitudExitoso() {
        UUID solicitudId = UUID.randomUUID();
        Solicitud solicitud = Solicitud.builder().id(solicitudId).estado(Estado.EN_VALIDACION_AUTOMATICA).build();
        DecisionSolicitud decision = DecisionSolicitud.builder()
                .solicitudId(solicitudId)
                .decision(Estado.APROBADA)
                .build();

        when(solicitudRepository.findById(solicitudId.toString())).thenReturn(Mono.just(solicitud));
        when(solicitudRepository.update(any(Solicitud.class))).thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));
        when(notificacionAutomaticaRepository.enviarCorreoSolicitud(any(DecisionSolicitud.class), any(Solicitud.class))).thenReturn(Mono.empty());
        when(reporteCambioEstadoRepository.enviarReporteSolicitud(any(DecisionSolicitud.class), any(Solicitud.class))).thenReturn(Mono.empty());

        StepVerifier.create(cambiarEstadoSolicitudUseCase.cambiarEstadoReporte(decision, "AUTOMATICA"))
                .expectNextMatches(s -> s.getEstado() == Estado.APROBADA)
                .verifyComplete();
    }

    @Test
    @DisplayName("Cambiar estado de solicitud - Falla por solicitud no encontrada")
    void cambiarEstadoSolicitudNoEncontrada() {
        UUID solicitudId = UUID.randomUUID(); // Genera un UUID válido
        DecisionSolicitud decision = DecisionSolicitud.builder()
                .solicitudId(solicitudId)
                .decision(Estado.APROBADA)
                .build();

        when(solicitudRepository.findById(solicitudId.toString())).thenReturn(Mono.empty());

        StepVerifier.create(cambiarEstadoSolicitudUseCase.cambiarEstadoReporte(decision, "MANUAL"))
                .expectErrorMatches(throwable -> throwable instanceof DomainException && "Solicitud no encontrada".equals(throwable.getMessage()))
                .verify();
    }

    @Test
    @DisplayName("Cambiar estado con DecisionSolicitud - Exitoso")
    void cambiarEstadoConDecisionSolicitudExitoso() {
        UUID solicitudId = UUID.randomUUID();
        DecisionSolicitud decision = DecisionSolicitud.builder()
                .solicitudId(solicitudId)
                .decision(Estado.APROBADA)
                .build();
        Solicitud solicitud = Solicitud.builder().id(solicitudId).estado(Estado.EN_VALIDACION_AUTOMATICA).build();

        when(solicitudRepository.findById(solicitudId.toString())).thenReturn(Mono.just(solicitud));
        when(solicitudRepository.update(any(Solicitud.class))).thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));
        when(reporteCambioEstadoRepository.enviarReporteSolicitud(any(DecisionSolicitud.class), any(Solicitud.class))).thenReturn(Mono.empty());
        when(notificacionAutomaticaRepository.enviarCorreoSolicitud(any(DecisionSolicitud.class), any(Solicitud.class))).thenReturn(Mono.empty());

        StepVerifier.create(cambiarEstadoSolicitudUseCase.cambiarEstadoReporte(decision, "AUTOMATICA"))
                .expectNextMatches(s -> s.getEstado() == Estado.APROBADA)
                .verifyComplete();
    }

    @Test
    @DisplayName("Cambiar estado con DecisionSolicitud - Falla por solicitud no encontrada")
    void cambiarEstadoConDecisionSolicitudNoEncontrada() {
        UUID solicitudId = UUID.randomUUID();
        DecisionSolicitud decision = DecisionSolicitud.builder()
                .solicitudId(solicitudId)
                .decision(Estado.APROBADA)
                .build();

        when(solicitudRepository.findById(solicitudId.toString())).thenReturn(Mono.empty());

        StepVerifier.create(cambiarEstadoSolicitudUseCase.cambiarEstadoReporte(decision,"AUTOMATICA"))
                .expectErrorMatches(throwable -> throwable instanceof DomainException && "Solicitud no encontrada".equals(throwable.getMessage()))
                .verify();
    }

    @Test
    @DisplayName("Cambiar estado con DecisionSolicitud a PENDIENTE_REVISION - No notifica")
    void cambiarEstadoConDecisionSolicitudPendiente() {
        UUID solicitudId = UUID.randomUUID();
        DecisionSolicitud decision = DecisionSolicitud.builder()
                .solicitudId(solicitudId)
                .decision(Estado.PENDIENTE_REVISION)
                .build();
        Solicitud solicitud = Solicitud.builder().id(solicitudId).estado(Estado.EN_VALIDACION_AUTOMATICA).build();

        when(solicitudRepository.findById(solicitudId.toString())).thenReturn(Mono.just(solicitud));
        when(solicitudRepository.update(any(Solicitud.class))).thenAnswer(invocation -> {
            Solicitud s = invocation.getArgument(0);
            s.setEstado(Estado.PENDIENTE_REVISION);
            return Mono.just(s);
        });

        StepVerifier.create(cambiarEstadoSolicitudUseCase.cambiarEstadoReporte(decision,"AUTOMATICA"))
                .verifyComplete();
    }
}