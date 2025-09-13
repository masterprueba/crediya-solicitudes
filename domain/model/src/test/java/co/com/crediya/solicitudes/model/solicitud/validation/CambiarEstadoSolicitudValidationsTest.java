package co.com.crediya.solicitudes.model.solicitud.validation;

import co.com.crediya.solicitudes.model.exceptions.DomainException;
import co.com.crediya.solicitudes.model.solicitud.Estado;
import co.com.crediya.solicitudes.model.solicitud.Solicitud;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import reactor.test.StepVerifier;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CambiarEstadoSolicitudValidationsTest {


    @Test
    @DisplayName("validarEstadoAprobadooRechazado: acepta APROBADA y RECHAZADA")
    void validarEstadoAprobadooRechazado_valido() {
        var validator = CambiarEstadoSolicitudValidations.validarEstado();

        StepVerifier.create(validator.validar(null, "APROBADA"))
                .expectNext("APROBADA")
                .verifyComplete();

        StepVerifier.create(validator.validar(null, "RECHAZADA"))
                .expectNext("RECHAZADA")
                .verifyComplete();
    }

    @Test
    @DisplayName("validarEstadoAprobadooRechazado: rechaza estado inválido")
    void validarEstadoAprobadooRechazado_invalido() {
        var validator = CambiarEstadoSolicitudValidations.validarEstado();

        StepVerifier.create(validator.validar(null, "EN_GESTION"))
                .expectErrorSatisfies(throwable -> {
                    assertInstanceOf(DomainException.class, throwable);
                    assertEquals("El esttado es inválido", throwable.getMessage());
                })
                .verify();
    }

    @Test
    @DisplayName("validarYaEstaAprobadaoRechazada: falla si ya está APROBADA y se pide APROBADA")
    void validarYaEstaAprobadaoRechazada_yaAprobada() {
        var validator = CambiarEstadoSolicitudValidations.validarYaEstaAprobadaoRechazada();

        Solicitud solicitud = mock(Solicitud.class);
        when(solicitud.getEstado()).thenReturn(Estado.APROBADA);

        StepVerifier.create(validator.validar(solicitud, "APROBADA"))
                .expectErrorSatisfies(throwable -> {
                    assertInstanceOf(DomainException.class, throwable);
                    assertEquals("La solicitud ya se encuentra aprobada", throwable.getMessage());
                })
                .verify();
    }

    @Test
    @DisplayName("validarYaEstaAprobadaoRechazada: falla si ya está RECHAZADA y se pide RECHAZADA")
    void validarYaEstaAprobadaoRechazada_yaRechazada() {
        var validator = CambiarEstadoSolicitudValidations.validarYaEstaAprobadaoRechazada();

        Solicitud solicitud = mock(Solicitud.class);
        when(solicitud.getEstado()).thenReturn(Estado.RECHAZADA);

        StepVerifier.create(validator.validar(solicitud, "RECHAZADA"))
                .expectErrorSatisfies(throwable -> {
                    assertInstanceOf(DomainException.class, throwable);
                    assertEquals("La solicitud ya se encuentra rechazada", throwable.getMessage());
                })
                .verify();
    }

    @Test
    @DisplayName("validarYaEstaAprobadaoRechazada: permite transición si no es duplicada")
    void validarYaEstaAprobadaoRechazada_transicionValida() {
        var validator = CambiarEstadoSolicitudValidations.validarYaEstaAprobadaoRechazada();

        Solicitud solicitud = mock(Solicitud.class);
        when(solicitud.getEstado()).thenReturn(Estado.APROBADA);

        StepVerifier.create(validator.validar(solicitud, "RECHAZADA"))
                .expectNext("RECHAZADA")
                .verifyComplete();
    }

    @Test
    @DisplayName("completa: falla con estado inválido")
    void completa_estadoInvalido() {
        var validator = CambiarEstadoSolicitudValidations.completa();

        Solicitud solicitud = mock(Solicitud.class);
        when(solicitud.getEstado()).thenReturn(Estado.APROBADA);

        StepVerifier.create(validator.validar(solicitud, "EN_GESTION"))
                .expectErrorSatisfies(throwable -> {
                    assertInstanceOf(DomainException.class, throwable);
                    assertEquals("El esttado es inválido", throwable.getMessage());
                })
                .verify();
    }

    @Test
    @DisplayName("completa: pasa con estado válido y no duplicado")
    void completa_validoNoDuplicado() {
        var validator = CambiarEstadoSolicitudValidations.completa();

        Solicitud solicitud = mock(Solicitud.class);
        when(solicitud.getEstado()).thenReturn(Estado.RECHAZADA);

        StepVerifier.create(validator.validar(solicitud, "APROBADA"))
                .expectNext("APROBADA")
                .verifyComplete();
    }

    @Test
    @DisplayName("Constructor privado: lanza DomainException")
    void constructor_privado_lanzaExcepcion() throws Exception {
        Constructor<CambiarEstadoSolicitudValidations> ctor =
                CambiarEstadoSolicitudValidations.class.getDeclaredConstructor();
        ctor.setAccessible(true);

        InvocationTargetException ex = assertThrows(InvocationTargetException.class, ctor::newInstance);
        assertNotNull(ex.getCause());
        assertInstanceOf(DomainException.class, ex.getCause());
        assertEquals("CambiarEstado class", ex.getCause().getMessage());
    }
}
