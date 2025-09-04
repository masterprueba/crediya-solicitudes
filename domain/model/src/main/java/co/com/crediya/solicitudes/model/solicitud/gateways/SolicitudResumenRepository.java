package co.com.crediya.solicitudes.model.solicitud.gateways;

import co.com.crediya.solicitudes.model.solicitud.Estado;
import co.com.crediya.solicitudes.model.solicitud.SolicitudResumen;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Set;

public interface SolicitudResumenRepository {
    Flux<SolicitudResumen> listarBase(Set<Estado> estados, int page, int size, String filtroTipo);
    Mono<Long> contar(Set<Estado> estados, String filtroTipo);
}