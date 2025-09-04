package co.com.crediya.solicitudes.model.cliente.validation;

import co.com.crediya.solicitudes.model.cliente.ClienteToken;
import reactor.core.publisher.Mono;

@FunctionalInterface
public interface ClienteValidation {
    Mono<ClienteToken> validar(ClienteToken clienteToken);

    default ClienteValidation and(ClienteValidation other) {
        return clienteToken -> this.validar(clienteToken)
        .flatMap(other::validar);
    }
}
