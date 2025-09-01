package co.com.crediya.solicitudes.model.cliente.gateways;

import co.com.crediya.solicitudes.model.cliente.Cliente;
import co.com.crediya.solicitudes.model.cliente.ClienteToken;
import reactor.core.publisher.Mono;

public interface ClienteRepository {
    Mono<Cliente> obtenerClientePorEmail(ClienteToken clienteToken);
}
