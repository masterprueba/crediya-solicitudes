package co.com.crediya.solicitudes.model.solicitud.gateways;

import reactor.core.publisher.Mono;

public interface CatalogoPrestamoRepository {
    Mono<Boolean> esTipoValido(String tipoPrestamo);
}
