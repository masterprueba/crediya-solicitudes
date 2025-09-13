package co.com.crediya.solicitudes.model.cliente.validation;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import reactor.test.StepVerifier;

import java.lang.reflect.InvocationTargetException;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("Cliente Validations Test")
class ClienteValidationsTest {


    @Test
    @DisplayName("Constructor privado para cobertura")
    void shouldThrowExceptionWhenInstantiating() throws Exception {
        java.lang.reflect.Constructor<ClienteValidations> constructor = ClienteValidations.class.getDeclaredConstructor();
        assertTrue(java.lang.reflect.Modifier.isPrivate(constructor.getModifiers()));
        constructor.setAccessible(true);

        InvocationTargetException thrown = assertThrows(InvocationTargetException.class, constructor::newInstance);
        Throwable cause = thrown.getTargetException();
        assertTrue(cause instanceof IllegalStateException);
    }

    @Test
    @DisplayName("Validar cliente no autorizado para la solicitud")
    void validarClienteNoAutorizadoParaSolicitud() {
        var solicitud = co.com.crediya.solicitudes.model.solicitud.Solicitud.builder()
                .email("test@test.com")
                .build();
        StepVerifier.create(ClienteValidations.validarClienteCreaSolicitudPropia(solicitud).validar("test@test.com"))
                .expectNextCount(1)
                .verifyComplete();
    }

    @Test
    @DisplayName("Validar cadena completa de validaciones")
    void validarCadenaCompletaDeValidaciones() {
        var solicitud = co.com.crediya.solicitudes.model.solicitud.Solicitud.builder()
                .email("test2@test.com")
                .build();
        StepVerifier.create(ClienteValidations.completa(solicitud).validar("test@test.com"))
                .expectErrorMessage("cliente_no_autorizado_para_esta_solicitud")
                .verify();
    }
}
