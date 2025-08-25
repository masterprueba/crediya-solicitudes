package co.com.crediya.solicitudes.usecase.crearsolicitud;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import co.com.crediya.solicitudes.model.solicitud.Estado;
import co.com.crediya.solicitudes.model.solicitud.Solicitud;
import co.com.crediya.solicitudes.model.solicitud.exceptions.DomainException;
import co.com.crediya.solicitudes.model.solicitud.gateways.CatalogoPrestamoRepository;
import co.com.crediya.solicitudes.model.solicitud.gateways.ClienteRepository;
import co.com.crediya.solicitudes.model.solicitud.gateways.SolicitudRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;
@RequiredArgsConstructor
public class CrearSolicitudUseCase {
    private final SolicitudRepository repo;
  private final ClienteRepository clientePort;
  private final CatalogoPrestamoRepository catalogoPort;

  public Mono<Solicitud> ejecutar(Solicitud s) {
        // Paso 1: Validaciones y obtención del ID del tipo de préstamo
        Mono<Solicitud> solicitudEnriquecida = validar(s)
                .flatMap(solicitudValidada -> clientePort.obtenerClientePorEmail(solicitudValidada.getEmail())
                        .switchIfEmpty(Mono.error(new DomainException("cliente_no_existe")))
                        .map(cliente -> solicitudValidada.toBuilder()
                                .nombres(cliente.getUsuario())
                                .documento_identidad(cliente.getDocumento_identidad())
                                .build()))
                .flatMap(val -> catalogoPort.esTipoValido(val.getTipoPrestamo())
                        .flatMap(ok -> ok ? Mono.just(val) : Mono.error(new DomainException("tipo_prestamo_invalido"))))
                .flatMap(val -> catalogoPort.obtenerIdPorNombre(val.getTipoPrestamo())
                        .map(tipoId -> val.toBuilder()
                                .id(UUID.randomUUID())
                                .tipoPrestamoId(tipoId)
                                .estado(Estado.PENDIENTE_REVISION)
                                .created(Instant.now())
                                .build()));
        
        // Paso 2: Guardar la solicitud enriquecida
        return solicitudEnriquecida
                .flatMap(solicitudAGuardar -> repo.save(solicitudAGuardar));
    }

  private Mono<Solicitud> validar(Solicitud s) {
    if (s.getMonto()==null || s.getMonto().compareTo(BigDecimal.ZERO)<=0)
      return Mono.error(new DomainException("monto_invalido"));
    if (s.getPlazoMeses()==null || s.getPlazoMeses()<=0)
      return Mono.error(new DomainException("plazo_invalido"));
    if (s.getTipoPrestamo()==null || s.getTipoPrestamo().isBlank())
      return Mono.error(new DomainException("tipo_requerido"));
    return Mono.just(s);
  }
}
