package co.com.crediya.solicitudes.usecase.listarsolicitudes;

import co.com.crediya.solicitudes.model.cliente.Cliente;
import co.com.crediya.solicitudes.model.cliente.ClienteToken;
import co.com.crediya.solicitudes.model.cliente.gateways.ClienteRepository;
import co.com.crediya.solicitudes.model.solicitud.Estado;

import co.com.crediya.solicitudes.model.solicitud.SolicitudResumen;
import co.com.crediya.solicitudes.model.solicitud.gateways.SolicitudResumenRepository;
import co.com.crediya.solicitudes.model.util.Pagina;
import lombok.RequiredArgsConstructor;

import reactor.core.publisher.Mono;
import reactor.util.Logger;
import reactor.util.Loggers;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;


@RequiredArgsConstructor
public class ListarSolicitudesUseCase {

    private final SolicitudResumenRepository solicitudResumenRepository;
    private final ClienteRepository clienteRepository;

    private  static final Logger log = Loggers.getLogger(ListarSolicitudesUseCase.class);

    public Mono<Pagina<SolicitudResumen>> listarSolicitudes(int page, int size, String filtroTipo, ClienteToken clienteToken) {

        log.info("Iniciando listado de solicitudes - página: {}, tamaño: {}, filtroTipo: {}, clienteEmail: {}",
                page, size, filtroTipo, clienteToken.getEmail());
        var estados = Set.of(Estado.PENDIENTE_REVISION, Estado.RECHAZADA, Estado.REVISION_MANUAL);

        Mono<List<SolicitudResumen>> solicitudesBaseMono = solicitudResumenRepository.listarBase(estados, page, size, filtroTipo)
                .collectList();

        return Mono.zip(solicitudesBaseMono, solicitudResumenRepository.contar(estados, filtroTipo))
                .flatMap(tuple -> {
                    List<SolicitudResumen> solicitudesBase = tuple.getT1();
                    Long totalElements = tuple.getT2();

                    if (solicitudesBase.isEmpty()) {
                        return Mono.just(new Pagina<SolicitudResumen>(
                                List.of(), page, size, totalElements, 0, false));
                    }

                    // ✅ Aquí el truco: devolvemos un Mono<List<SolicitudResumen>>
                    Mono<List<SolicitudResumen>> solicitudesEnriquecidas =
                            clienteRepository.obtenerClientes(clienteToken) // Flux<Cliente>
                                    .collectMap(Cliente::getEmail, cliente -> cliente) // Mono<Map<String, Cliente>>
                                    .map(mapaClientes -> solicitudesBase.stream()
                                            .map(base -> {
                                                Cliente cliente = mapaClientes.get(base.getEmail());
                                                if (cliente != null) {
                                                    return new SolicitudResumen(
                                                            base.getId(), cliente.getDocumentoIdentidad(), base.getMonto(), base.getPlazoMeses(),
                                                            base.getTipoPrestamo(), base.getTasaInteres(), base.getEstado(),
                                                            cliente.getUsuario(), cliente.getEmail(), cliente.getSalarioBase(),
                                                            base.getDeudaTotalMensualAprobadas()
                                                    );
                                                } else {
                                                    return new SolicitudResumen(
                                                            base.getId(), null, base.getMonto(), base.getPlazoMeses(),
                                                            base.getTipoPrestamo(), base.getTasaInteres(), base.getEstado(),
                                                            "Usuario no encontrado", base.getEmail(), BigDecimal.ZERO,
                                                            base.getDeudaTotalMensualAprobadas()
                                                    );
                                                }
                                            })
                                            .toList()
                                    );

                    // ✅ construimos la página dentro del flatMap
                    return solicitudesEnriquecidas.map(lista -> {
                        var totalPages = (int) Math.ceil((double) totalElements / size);
                        var hasNext = page + 1 < totalPages;
                        return new Pagina<>(lista, page, size, totalElements, totalPages, hasNext);
                    });
                });
    }
}
