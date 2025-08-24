package co.com.crediya.solicitudes.model.solicitud.gateways;

import reactor.core.publisher.Mono;

public interface CatalogoPrestamo {
    Mono<Boolean> esTipoValido(String tipoPrestamo);
}
