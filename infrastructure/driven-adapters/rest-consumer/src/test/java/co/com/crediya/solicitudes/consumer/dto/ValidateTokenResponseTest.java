package co.com.crediya.solicitudes.consumer.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ValidateTokenResponseTest {

    @Test
    void constructorAndAccessors_shouldWorkCorrectly() {
        String userId = "user123";
        String email = "test@dominio.com";
        String role = "ROL_USUARIO";

        ValidateTokenResponse response = new ValidateTokenResponse(userId, email, role);

        assertEquals(userId, response.userId());
        assertEquals(email, response.email());
        assertEquals(role, response.role());
    }

    @Test
    void equalsAndHashCode_shouldWorkCorrectly() {
        ValidateTokenResponse r1 = new ValidateTokenResponse("id", "mail", "rol");
        ValidateTokenResponse r2 = new ValidateTokenResponse("id", "mail", "rol");
        ValidateTokenResponse r3 = new ValidateTokenResponse("id2", "mail2", "rol2");

        assertEquals(r1, r2);
        assertEquals(r1.hashCode(), r2.hashCode());
        assertNotEquals(r1, r3);
    }

    @Test
    void toString_shouldContainFieldValues() {
        ValidateTokenResponse response = new ValidateTokenResponse("id", "mail", "rol");
        String str = response.toString();
        assertTrue(str.contains("id"));
        assertTrue(str.contains("mail"));
        assertTrue(str.contains("rol"));
    }
}