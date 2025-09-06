package co.com.crediya.solicitudes.model.cliente.validation;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import reactor.test.StepVerifier;

@DisplayName("Cliente Validations Test")
class ClienteValidationsTest {



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
