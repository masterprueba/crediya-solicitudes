package co.com.crediya.solicitudes.model.auth;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

@DisplayName("AuthenticatedUser Test")
class AuthenticatedUserTest {

    @Test
    @DisplayName("Debe crear una instancia de AuthenticatedUser")
    void debeCrearInstanciaDeAuthenticatedUser() {
        AuthenticatedUser user = new AuthenticatedUser("1", "test@test.com", "CLIENTE", "token123");
        assertNotNull(user);
    }

    @Test
    @DisplayName("Debe crear una instancia de AuthenticatedUser sin los atributos")
    void debeCrearInstanciaDeAuthenticatedUserSinAtributos() {
        AuthenticatedUser user = new AuthenticatedUser();
        assertNotNull(user);
    }

    @Test
    @DisplayName("Debe crear una instancia de AuthenticatedUser con el builder")
    void debeCrearInstanciaDeAuthenticatedUserConElBuilder() {
        AuthenticatedUser user = AuthenticatedUser.builder()
                .userId("1")
                .email("test@test.com")
                .role("CLIENTE")
                .token("token123")
                .build();
        assertNotNull(user);
    }

    @Test
    @DisplayName("Debe tener setters y getters")
    void debeTenerSettersYGetters() {
        AuthenticatedUser user = new AuthenticatedUser();
        user.setUserId("1");
        user.setEmail("test@test.com");
        user.setRole("CLIENTE");
        user.setToken("token122");
        assertEquals("1", user.getUserId());
        assertEquals("test@test.com", user.getEmail());
        assertEquals("CLIENTE", user.getRole());
        assertEquals("token122", user.getToken());
    }

    @Test
    @DisplayName("Debe validar si el rol es CLIENTE")
    void debeValidarSiEsCliente() {
        AuthenticatedUser cliente = new AuthenticatedUser("1", "test@cliente.com", "CLIENTE", "token");
        AuthenticatedUser noCliente = new AuthenticatedUser("2", "test@otro.com", "ASESOR", "token");

        assertTrue(cliente.isCliente());
        assertFalse(noCliente.isCliente());
    }

    @Test
    @DisplayName("Debe comparar objetos y validar hashCode")
    void debeCompararObjetosYHashCode() {
        AuthenticatedUser user1 = new AuthenticatedUser("1", "test@test.com", "CLIENTE", "token123");
        AuthenticatedUser user2 = new AuthenticatedUser("1", "test@test.com", "CLIENTE", "token123");
        AuthenticatedUser user3 = new AuthenticatedUser("2", "test@otro.com", "ASESOR", "token456");
        AuthenticatedUser user4 = new AuthenticatedUser("1", "test@test.com", "CLIENTE", "tokenDiferente");


        assertEquals(user1, user2);
        assertEquals(user1.hashCode(), user2.hashCode());
        assertNotNull(user1.toString());


        assertNotEquals(user1, user3);
        assertNotEquals(user1.hashCode(), user3.hashCode());


        assertNotEquals(user1, user4);
        assertNotNull(user1);
        assertNotEquals(user1, new Object());
    }
}
