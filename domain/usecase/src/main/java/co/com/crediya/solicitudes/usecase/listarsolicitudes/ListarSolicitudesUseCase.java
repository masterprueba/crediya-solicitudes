package co.com.crediya.solicitudes.usecase.listarsolicitudes;

import co.com.crediya.solicitudes.model.cliente.ClienteToken;
import co.com.crediya.solicitudes.model.cliente.gateways.ClienteRepository;
import co.com.crediya.solicitudes.model.exceptions.DomainException;
import co.com.crediya.solicitudes.model.solicitud.Estado;

import co.com.crediya.solicitudes.model.solicitud.SolicitudResumen;
import co.com.crediya.solicitudes.model.solicitud.gateways.SolicitudResumenRepository;
import co.com.crediya.solicitudes.model.util.Pagina;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;
import reactor.util.Logger;
import reactor.util.Loggers;

import java.math.BigDecimal;
import java.util.Set;


@RequiredArgsConstructor
public class ListarSolicitudesUseCase {

    private final SolicitudResumenRepository solicitudResumenRepository;
    private final ClienteRepository clienteRepository;

    private final static Logger log = Loggers.getLogger(ListarSolicitudesUseCase.class);

    public Mono<Pagina<SolicitudResumen>> listarSolicitudes(int page, int size, String filtroTipo, ClienteToken clienteToken) {

        var estados = Set.of(Estado.PENDIENTE_REVISION, Estado.RECHAZADA, Estado.REVISION_MANUAL);

        var solicitudesBase = solicitudResumenRepository.listarBase(estados, page, size, filtroTipo)
                .flatMap(base ->{
                                ClienteToken clienteToken1 = clienteToken.toBuilder().email(base.getEmail()).build();
                                return clienteRepository.obtenerClientePorEmail(clienteToken1)
                                        .onErrorResume(e -> Mono.error(new DomainException("Error al obtener cliente por email")))
                                        .map(cliente -> new SolicitudResumen(
                                                base.getId(), cliente.getDocumentoIdentidad(), base.getMonto(), base.getPlazoMeses(),
                                                base.getTipoPrestamo(), base.getTasaInteres(), base.getEstado(),
                                                cliente.getUsuario(), cliente.getEmail(), cliente.getSalarioBase(),
                                                base.getDeudaTotalMensualAprobadas()
                                        ))
                                        .switchIfEmpty(Mono.just(new SolicitudResumen(
                                                base.getId(), null, base.getMonto(), base.getPlazoMeses(),
                                                base.getTipoPrestamo(), base.getTasaInteres(), base.getEstado(),
                                                "Usuario no encontrado", base.getEmail(), BigDecimal.ZERO,
                                                base.getDeudaTotalMensualAprobadas()
                                        )));
                                },8)
                .collectList();

        var totalElementsMono = solicitudResumenRepository.contar(estados, filtroTipo);

        return Mono.zip(solicitudesBase, totalElementsMono)
                .map(tuple -> {
                    log.info("Tuple: {}", tuple.getT1().size());
                    var solicitudes = tuple.getT1();
                    var totalElements = tuple.getT2();
                    var totalPages = (int) Math.ceil((double) totalElements / size);
                    var hasNext = page + 1 < totalPages;
                    return new Pagina<>(solicitudes, page, size, totalElements, totalPages, hasNext);
                });
    }
}
