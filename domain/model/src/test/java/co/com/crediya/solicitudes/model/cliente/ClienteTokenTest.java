package co.com.crediya.solicitudes.model.cliente;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("ClienteToken Test")
class ClienteTokenTest {

    @Test
    void constructorAndGetters_shouldWorkCorrectly() {
        ClienteToken token = new ClienteToken("id1", "mail@dom.com", "ROL", "token123");
        assertEquals("id1", token.getUserId());
        assertEquals("mail@dom.com", token.getEmail());
        assertEquals("ROL", token.getRole());
        assertEquals("token123", token.getToken());
    }

    @Test
    void builder_shouldBuildCorrectly() {
        ClienteToken token = ClienteToken.builder()
                .userId("id2")
                .email("mail2@dom.com")
                .role("ROL2")
                .token("token456")
                .build();
        assertEquals("id2", token.getUserId());
        assertEquals("mail2@dom.com", token.getEmail());
        assertEquals("ROL2", token.getRole());
        assertEquals("token456", token.getToken());
    }

    @Test
    void equalsAndHashCode_shouldWorkCorrectly() {
        ClienteToken t1 = new ClienteToken("id", "mail", "rol", "token");
        ClienteToken t2 = new ClienteToken("id", "mail", "rol", "token");
        ClienteToken t3 = new ClienteToken("idx", "mailx", "rolx", "tokenx");
        assertEquals(t1, t2);
        assertEquals(t1.hashCode(), t2.hashCode());
        assertNotEquals(t1, t3);
    }

    @Test
    void toString_shouldContainFieldValues() {
        ClienteToken token = new ClienteToken("id", "mail", "rol", "token");
        String str = token.toString();
        assertTrue(str.contains("id"));
        assertTrue(str.contains("mail"));
        assertTrue(str.contains("rol"));
        assertTrue(str.contains("token"));
    }
}
