package co.com.crediya.solicitudes.model.cliente.validation;

import co.com.crediya.solicitudes.model.exceptions.DomainException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@DisplayName("ClienteValidation Test")
class ClienteValidationTest {

    @Test
    @DisplayName("Encadenar validaciones con 'and' - Exitoso")
    void testAndValidationSuccess() {
        ClienteValidation validation1 = email -> Mono.just(email.toUpperCase());
        ClienteValidation validation2 = email -> Mono.just(email + "_VALIDATED");

        ClienteValidation combinedValidation = validation1.and(validation2);

        StepVerifier.create(combinedValidation.validar("test@test.com"))
                .expectNext("TEST@TEST.COM_VALIDATED")
                .verifyComplete();
    }

    @Test
    @DisplayName("Encadenar validaciones con 'and' - Falla la primera")
    void testAndValidationFailureFirst() {
        ClienteValidation validation1 = email -> Mono.error(new DomainException("ERROR1"));
        ClienteValidation validation2 = email -> Mono.just(email + "_VALIDATED");

        ClienteValidation combinedValidation = validation1.and(validation2);

        StepVerifier.create(combinedValidation.validar("test@test.com"))
                .expectErrorMatches(throwable -> throwable instanceof DomainException && "ERROR1".equals(throwable.getMessage()))
                .verify();
    }

    @Test
    @DisplayName("Encadenar validaciones con 'and' - Falla la segunda")
    void testAndValidationFailureSecond() {
        ClienteValidation validation1 = email -> Mono.just(email.toUpperCase());
        ClienteValidation validation2 = email -> Mono.error(new DomainException("ERROR2"));

        ClienteValidation combinedValidation = validation1.and(validation2);

        StepVerifier.create(combinedValidation.validar("test@test.com"))
                .expectErrorMatches(throwable -> throwable instanceof DomainException && "ERROR2".equals(throwable.getMessage()))
                .verify();
    }
}
