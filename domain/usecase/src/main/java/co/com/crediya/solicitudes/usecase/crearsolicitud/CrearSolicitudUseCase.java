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
     return validar(s)
      .flatMap(val -> clientePort.existeClientePorDocumento(val.getDocumentoCliente())
        .flatMap(existe -> existe ? Mono.just(val)
                                  : Mono.error(new DomainException("cliente_no_existe"))))
        .flatMap(val -> catalogoPort.esTipoValido(val.getTipoPrestamoId().toString())
             .flatMap(ok -> ok ? Mono.just(val)
                               : Mono.error(new DomainException("tipo_prestamo_invalido"))))
        .map(val -> new Solicitud(
            UUID.randomUUID(), val.getDocumentoCliente(), val.getEmail(),
            val.getMonto(), val.getPlazoMeses(), val.getTipoPrestamoId(),
            Estado.PENDIENTE_REVISION, Instant.now()))
          .flatMap(repo::save);
 
  }

  private Mono<Solicitud> validar(Solicitud s) {
    if (s.getDocumentoCliente()==null || s.getDocumentoCliente().isBlank())
      return Mono.error(new DomainException("documento_requerido"));
    if (s.getMonto()==null || s.getMonto().compareTo(BigDecimal.ZERO)<=0)
      return Mono.error(new DomainException("monto_invalido"));
    if (s.getPlazoMeses()==null || s.getPlazoMeses()<=0)
      return Mono.error(new DomainException("plazo_invalido"));
    if (s.getTipoPrestamoId()==null || s.getTipoPrestamoId().toString().isBlank())
      return Mono.error(new DomainException("tipo_requerido"));
    return Mono.just(s);
  }
}
