package co.com.crediya.solicitudes.model.exceptions;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
 import static org.junit.jupiter.api.Assertions.assertNotNull;

@DisplayName("DomainException Test")
class DomainExceptionTest {

    @Test
    @DisplayName("Crear DomainException con mensaje - Exitoso")
    void testCrearDomainExceptionConMensaje() {
        String errorMessage = "Este es un error de dominio.";
        DomainException exception = new DomainException(errorMessage);

        assertNotNull(exception);
        assertEquals(errorMessage, exception.getMessage());
    }
}
