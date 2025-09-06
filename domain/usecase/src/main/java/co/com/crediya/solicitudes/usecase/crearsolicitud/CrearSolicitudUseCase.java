package co.com.crediya.solicitudes.usecase.crearsolicitud;

import java.time.Instant;
import java.util.UUID;

import co.com.crediya.solicitudes.model.cliente.gateways.ClienteRepository;
import co.com.crediya.solicitudes.model.exceptions.DomainException;
import co.com.crediya.solicitudes.model.solicitud.Estado;
import co.com.crediya.solicitudes.model.solicitud.Solicitud;
import co.com.crediya.solicitudes.model.solicitud.gateways.CatalogoPrestamoRepository;
import co.com.crediya.solicitudes.model.solicitud.gateways.SolicitudRepository;
import co.com.crediya.solicitudes.model.cliente.validation.ClienteValidations;
import co.com.crediya.solicitudes.model.solicitud.validation.SolicitudValidations;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class CrearSolicitudUseCase {
    private final SolicitudRepository repo;
  private final ClienteRepository clientePort;
  private final CatalogoPrestamoRepository catalogoPort;

  public Mono<Solicitud> ejecutar(Solicitud soliciitud, String email) {
        Mono<Solicitud> solicitudEnriquecida = SolicitudValidations.completa()
                .validar(soliciitud)
                .flatMap(soliciitudValida -> ClienteValidations.completa(soliciitudValida)
                        .validar(email)
                        .map(cliente -> soliciitudValida.toBuilder()
                                .email(email)
                                .build()))
                .flatMap(solicitudValidada -> clientePort.obtenerClientePorEmail(email)
                        .switchIfEmpty(Mono.error(new DomainException("cliente no existe o no esta autorizado")))
                        .map(cliente -> solicitudValidada.toBuilder()
                                .nombres(cliente.getUsuario())
                                .documentoIdentidad(cliente.getDocumentoIdentidad())
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

        return solicitudEnriquecida
                .flatMap(repo::save);
    }

}
