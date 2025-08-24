package co.com.crediya.solicitudes.model.solicitud.gateways;

import reactor.core.publisher.Mono;

public interface ClienteRepository {
    Mono<Boolean> existeClientePorDocumento(String documento);
}
