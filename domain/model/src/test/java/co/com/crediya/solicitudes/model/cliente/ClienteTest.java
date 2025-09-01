package co.com.crediya.solicitudes.model.cliente;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DisplayName("Cliente Test")
class ClienteTest {

    @Test
    @DisplayName("Debe crear instancia con constructor sin argumentos")
    void debeCrearInstanciaVacia() {
        Cliente cliente = new Cliente();
        assertNotNull(cliente);
    }

    @Test
    @DisplayName("Debe crear instancia con todos los argumentos")
    void debeCrearInstanciaConTodosLosArgumentos() {
        Cliente cliente = new Cliente("Test User", "test@test.com", "12345");
        assertEquals("Test User", cliente.getUsuario());
        assertEquals("test@test.com", cliente.getEmail());
        assertEquals("12345", cliente.getDocumento_identidad());
    }

    @Test
    @DisplayName("Debe crear instancia con builder")
    void debeCrearInstanciaConBuilder() {
        Cliente cliente = Cliente.builder()
                .usuario("Builder User")
                .email("builder@test.com")
                .documento_identidad("54321")
                .build();
        assertEquals("Builder User", cliente.getUsuario());
        assertEquals("builder@test.com", cliente.getEmail());
        assertEquals("54321", cliente.getDocumento_identidad());
    }

    @Test
    @DisplayName("Debe tener getters y setters funcionales")
    void debeTenerGettersYSetters() {
        Cliente cliente = new Cliente();
        cliente.setUsuario("Setter User");
        cliente.setEmail("setter@test.com");
        cliente.setDocumento_identidad("67890");

        assertEquals("Setter User", cliente.getUsuario());
        assertEquals("setter@test.com", cliente.getEmail());
        assertEquals("67890", cliente.getDocumento_identidad());
    }

    @Test
    @DisplayName("Debe comparar objetos y validar hashCode")
    void debeCompararObjetosYHashCode() {
        Cliente cliente1 = new Cliente("User", "user@test.com", "111");
        Cliente cliente2 = new Cliente("User", "user@test.com", "111");
        Cliente cliente3 = new Cliente("Otro User", "otro@test.com", "222");

        assertEquals(cliente1, cliente2);
        assertEquals(cliente1.hashCode(), cliente2.hashCode());
        assertNotNull(cliente1.toString());

        assertNotEquals(cliente1, cliente3);
        assertNotEquals(cliente1.hashCode(), cliente3.hashCode());
    }
}
