package co.com.crediya.solicitudes.usecase.cambiarestadosolicitud;

import co.com.crediya.solicitudes.model.exceptions.DomainException;
import co.com.crediya.solicitudes.model.solicitud.Estado;
import co.com.crediya.solicitudes.model.solicitud.Solicitud;
import co.com.crediya.solicitudes.model.solicitud.gateways.NotificacionRepository;
import co.com.crediya.solicitudes.model.solicitud.gateways.SolicitudRepository;
import co.com.crediya.solicitudes.model.solicitud.validation.CambiarEstadoSolicitudValidations;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class CambiarEstadoSolicitudUseCase {
    private final SolicitudRepository solicitudRepository;
    private final NotificacionRepository notificacionRepository;

    public Mono<Solicitud> cambiarEstado(String solicitudId, String nuevoEstado) {
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
                .flatMap(solicitudGuardada ->
                    notificacionRepository.enviarDecisionSolicitud(solicitudGuardada)
                        .thenReturn(solicitudGuardada)
                );
    }



}
