package co.com.crediya.solicitudes.model.solicitud.gateways;

import co.com.crediya.solicitudes.model.cliente.Cliente;
import reactor.core.publisher.Mono;

public interface ClienteRepository {
    Mono<Cliente> obtenerClientePorEmail(String email);
}
