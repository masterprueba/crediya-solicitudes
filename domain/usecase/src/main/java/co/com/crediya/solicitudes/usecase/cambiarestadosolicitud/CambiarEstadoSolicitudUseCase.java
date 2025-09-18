package co.com.crediya.solicitudes.usecase.cambiarestadosolicitud;

import co.com.crediya.solicitudes.model.exceptions.DomainException;
import co.com.crediya.solicitudes.model.solicitud.Estado;
import co.com.crediya.solicitudes.model.solicitud.Solicitud;
import co.com.crediya.solicitudes.model.solicitud.gateways.NotificacionAutomaticaRepository;
import co.com.crediya.solicitudes.model.solicitud.gateways.NotificacionManualRepository;
import co.com.crediya.solicitudes.model.solicitud.gateways.ReporteCambioEstadoRepository;
import co.com.crediya.solicitudes.model.solicitud.gateways.SolicitudRepository;
import co.com.crediya.solicitudes.model.solicitud.DecisionSolicitud;
import co.com.crediya.solicitudes.model.solicitud.validation.CambiarEstadoSolicitudValidations;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;
import reactor.util.Logger;
import reactor.util.Loggers;

@RequiredArgsConstructor
public class CambiarEstadoSolicitudUseCase {

    private final Logger log = Loggers.getLogger(CambiarEstadoSolicitudUseCase.class);

    private final SolicitudRepository solicitudRepository;
    private final NotificacionManualRepository notificacionManualRepository;
    private final NotificacionAutomaticaRepository notificacionAutomaticaRepository;
    private final ReporteCambioEstadoRepository reporteCambioEstadoRepository;

    public Mono<Solicitud> cambiarEstadoReporte(DecisionSolicitud decision, String tipoNotificacion) {
        String solicitudId = decision.getSolicitudId().toString();
        String nuevoEstado = decision.getDecision().name();
        return
                solicitudRepository.findById(solicitudId)
                .switchIfEmpty(Mono.error(new DomainException("Solicitud no encontrada")))
                .flatMap(solicitud -> CambiarEstadoSolicitudValidations.completa()
                        .validar(solicitud,nuevoEstado)
                        .thenReturn(solicitud))
                .flatMap(solicitud -> {
                    solicitud.setEstado(Estado.valueOf(nuevoEstado));
                    return solicitudRepository.update(solicitud);
                })
                        .filter(solicitud -> solicitud.getEstado() == Estado.APROBADA || solicitud.getEstado() == Estado.RECHAZADA)
                        .flatMap(solicitudGuardada -> {
                            Mono<Void> notificacionMono = tipoNotificacion.equals("MANUAL")
                                    ? notificacionManualRepository.enviarCorreoSolicitud(solicitudGuardada)
                                    : notificacionAutomaticaRepository.enviarCorreoSolicitud(decision, solicitudGuardada);
                            if (Estado.APROBADA.equals(solicitudGuardada.getEstado())) {
                                Mono<Void> reporteMono = reporteCambioEstadoRepository.enviarReporteSolicitud(decision, solicitudGuardada);
                                return Mono.when(reporteMono, notificacionMono).thenReturn(solicitudGuardada);
                            }
                            return notificacionMono.thenReturn(solicitudGuardada);
                        });
    }



}
