package co.com.crediya.solicitudes.model.solicitud.gateways;

import reactor.core.publisher.Mono;


import co.com.crediya.solicitudes.model.solicitud.TipoPrestamo;

public interface CatalogoPrestamoRepository {
    Mono<TipoPrestamo> obtenerTipoPrestamoPorNombre(String nombre);
}
