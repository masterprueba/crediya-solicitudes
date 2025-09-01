package co.com.crediya.solicitudes.consumer.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ValidateTokenRequestTest {

    @Test
    void constructorAndAccessor_shouldWorkCorrectly() {
        String token = "token123";
        ValidateTokenRequest request = new ValidateTokenRequest(token);
        assertEquals(token, request.token());
    }

    @Test
    void equalsAndHashCode_shouldWorkCorrectly() {
        ValidateTokenRequest r1 = new ValidateTokenRequest("tokenA");
        ValidateTokenRequest r2 = new ValidateTokenRequest("tokenA");
        ValidateTokenRequest r3 = new ValidateTokenRequest("tokenB");

        assertEquals(r1, r2);
        assertEquals(r1.hashCode(), r2.hashCode());
        assertNotEquals(r1, r3);
    }

    @Test
    void toString_shouldContainFieldValue() {
        ValidateTokenRequest request = new ValidateTokenRequest("tokenXYZ");
        String str = request.toString();
        assertTrue(str.contains("tokenXYZ"));
    }
}