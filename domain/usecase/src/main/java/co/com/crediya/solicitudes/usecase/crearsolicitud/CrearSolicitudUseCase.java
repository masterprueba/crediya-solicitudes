package co.com.crediya.solicitudes.usecase.crearsolicitud;

import java.time.Instant;
import java.util.UUID;

import co.com.crediya.solicitudes.model.cliente.gateways.ClienteRepository;
import co.com.crediya.solicitudes.model.exceptions.DomainException;
import co.com.crediya.solicitudes.model.solicitud.Estado;
import co.com.crediya.solicitudes.model.solicitud.Solicitud;
import co.com.crediya.solicitudes.model.solicitud.gateways.CatalogoPrestamoRepository;
import co.com.crediya.solicitudes.model.solicitud.gateways.SolicitudRepository;
import co.com.crediya.solicitudes.model.solicitud.validation.SolicitudValidations;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class CrearSolicitudUseCase {
    private final SolicitudRepository repo;
  private final ClienteRepository clientePort;
  private final CatalogoPrestamoRepository catalogoPort;

  public Mono<Solicitud> ejecutar(Solicitud s) {
        // Paso 1: Validaciones y obtención del ID del tipo de préstamo
        Mono<Solicitud> solicitudEnriquecida = SolicitudValidations.completa()
                .validar(s)
                .flatMap(solicitudValidada -> clientePort.obtenerClientePorEmail(solicitudValidada.getEmail())
                        .switchIfEmpty(Mono.error(new DomainException("cliente_no_existe")))
                        .map(cliente -> solicitudValidada.toBuilder()
                                .nombres(cliente.getUsuario())
                                .documentoIdentidad(cliente.getDocumento_identidad())
                                .build()))
                .flatMap(val -> catalogoPort.esTipoValido(val.getTipoPrestamo())
                        .flatMap(ok -> Boolean.TRUE.equals(ok) ? Mono.just(val) : Mono.error(new DomainException("tipo_prestamo_invalido"))))
                .flatMap(val -> catalogoPort.obtenerIdPorNombre(val.getTipoPrestamo())
                        .map(tipoId -> val.toBuilder()
                                .id(UUID.randomUUID())
                                .tipoPrestamoId(tipoId)
                                .estado(Estado.PENDIENTE_REVISION)
                                .created(Instant.now())
                                .build()));
        
        // Paso 2: Guardar la solicitud enriquecida
        return solicitudEnriquecida
                .flatMap(repo::save);
    }

}
