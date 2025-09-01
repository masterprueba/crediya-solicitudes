package co.com.crediya.solicitudes.model.solicitud.validation;

import co.com.crediya.solicitudes.model.cliente.ClienteToken;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import reactor.test.StepVerifier;

@DisplayName("Cliente Validations Test")
class ClienteValidationsTest {

    private ClienteToken base() {
        return ClienteToken.builder()
                .email("test@test.com")
                .role("CLIENTE")
                .userId("12345")
                .token("token")
                .build();
    }

    @Test
    @DisplayName("Validar cliente no autenticado")
    void validarClienteNoAutenticado() {
        StepVerifier.create(ClienteValidations.validarClienteYPermisos().validar(null))
                .expectErrorMessage("usuario_no_autenticado")
                .verify();
    }

    @Test
    @DisplayName("Validar cliente autenticado")
    void validarClienteAutenticado() {
        StepVerifier.create(ClienteValidations.validarClienteYPermisos().validar(base()))
                .expectNextCount(1)
                .verifyComplete();
    }

    @Test
    @DisplayName("Validar usuario no autorizado (no es cliente)")
    void validarUsuarioNoAutorizado() {
        var cliente = base().toBuilder().role("ADMIN").build();
        StepVerifier.create(ClienteValidations.validarSoloClientes().validar(cliente))
                .expectErrorMessage("usuario_no_autorizado")
                .verify();
    }

    @Test
    @DisplayName("Validar usuario autorizado (es cliente)")
    void validarUsuarioAutorizado() {
        StepVerifier.create(ClienteValidations.validarSoloClientes().validar(base()))
                .expectNextCount(1)
                .verifyComplete();
    }

    @Test
    @DisplayName("Validar cliente no autorizado para la solicitud")
    void validarClienteNoAutorizadoParaSolicitud() {
        var solicitud = co.com.crediya.solicitudes.model.solicitud.Solicitud.builder()
                .email("test@test.com")
                .build();
        StepVerifier.create(ClienteValidations.validarClienteCreaSolicitudPropia(solicitud).validar(base()))
                .expectNextCount(1)
                .verifyComplete();
    }

    @Test
    @DisplayName("Validar cadena completa de validaciones")
    void validarCadenaCompletaDeValidaciones() {
        var solicitud = co.com.crediya.solicitudes.model.solicitud.Solicitud.builder()
                .email("test2@test.com")
                .build();
        StepVerifier.create(ClienteValidations.completa(solicitud).validar(base()))
                .expectErrorMessage("cliente_no_autorizado_para_esta_solicitud")
                .verify();
    }
}
