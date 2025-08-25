package co.com.crediya.solicitudes.model.solicitud.validation;

import co.com.crediya.solicitudes.model.exceptions.DomainException;
import co.com.crediya.solicitudes.model.solicitud.Solicitud;
import org.junit.jupiter.api.Test;
import reactor.test.StepVerifier;

import java.math.BigDecimal;

class SolicitudValidationsTest {

    private Solicitud base() {
        return Solicitud.builder()
                .email("a@b.com")
                .monto(new BigDecimal("1000"))
                .plazoMeses(12)
                .tipoPrestamo("LIBRE_INVERSION")
                .build();
    }

    @Test
    void validarMonto_ok() {
        StepVerifier.create(SolicitudValidations.validarMonto().validar(base()))
                .expectNextCount(1)
                .verifyComplete();
    }

    @Test
    void validarMonto_error() {
        var s = base().toBuilder().monto(BigDecimal.ZERO).build();
        StepVerifier.create(SolicitudValidations.validarMonto().validar(s))
                .expectError(DomainException.class)
                .verify();
    }

    @Test
    void validarPlazo_ok() {
        StepVerifier.create(SolicitudValidations.validarPlazo().validar(base()))
                .expectNextCount(1)
                .verifyComplete();
    }

    @Test
    void validarPlazo_error() {
        var s = base().toBuilder().plazoMeses(0).build();
        StepVerifier.create(SolicitudValidations.validarPlazo().validar(s))
                .expectError(DomainException.class)
                .verify();
    }

    @Test
    void validarTipo_ok() {
        StepVerifier.create(SolicitudValidations.validarTipoPrestamo().validar(base()))
                .expectNextCount(1)
                .verifyComplete();
    }

    @Test
    void validarTipo_error() {
        var s = base().toBuilder().tipoPrestamo("   ").build();
        StepVerifier.create(SolicitudValidations.validarTipoPrestamo().validar(s))
                .expectError(DomainException.class)
                .verify();
    }

    @Test
    void completa_ok() {
        StepVerifier.create(SolicitudValidations.completa().validar(base()))
                .expectNextCount(1)
                .verifyComplete();
    }

    @Test
    void completa_errorPorMonto() {
        var s = base().toBuilder().monto(null).build();
        StepVerifier.create(SolicitudValidations.completa().validar(s))
                .expectError(DomainException.class)
                .verify();
    }
}
