package co.com.crediya.solicitudes.model.cliente.validation;

import reactor.core.publisher.Mono;

@FunctionalInterface
public interface ClienteValidation {
    Mono<String> validar(String email);

    default ClienteValidation and(ClienteValidation other) {
        return email -> this.validar(email)
        .flatMap(other::validar);
    }
}
