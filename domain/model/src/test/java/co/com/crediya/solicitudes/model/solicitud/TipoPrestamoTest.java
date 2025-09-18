package co.com.crediya.solicitudes.model.solicitud;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DisplayName("Tests for TipoPrestamo Value Object")
class TipoPrestamoTest {

    @Test
    @DisplayName("crear TipoPrestamo exitosamente")
    void crearTipoPrestamoExitosamente() {
        // Arrange
        TipoPrestamo tipoPrestamo = TipoPrestamo.builder()
                .id(java.util.UUID.randomUUID())
                .nombre("Personal")
                .build();

        // Act & Assert
        assertNotNull(tipoPrestamo);
        assertNotNull(tipoPrestamo.getId());
        assertEquals("Personal", tipoPrestamo.getNombre());
    }

    @Test
    @DisplayName("crear TipoPrestamo con todos los parametros")
    void crearTipoPrestamoConTodosLosParametros() {
        // Arrange
        java.util.UUID id = java.util.UUID.randomUUID();
        String nombre = "Hipotecario";

        // Act
        TipoPrestamo tipoPrestamo = new TipoPrestamo(id, nombre, 0.0, 0.0, 0.0, false);

        // Assert
        assertEquals(id, tipoPrestamo.getId());
        assertEquals(nombre, tipoPrestamo.getNombre());
    }

    @Test
    @DisplayName("crear TipoPrestamo con constructor vacio y luego setear valores")
    void crearTipoPrestamoConConstructorVacioYLuegoSetearValores() {
        // Arrange
        TipoPrestamo tipoPrestamo = new TipoPrestamo();
        java.util.UUID id = java.util.UUID.randomUUID();
        String nombre = "Personal";
        // Act
        tipoPrestamo.setId(id);
        tipoPrestamo.setNombre(nombre);
        // Assert
        assertEquals(id, tipoPrestamo.getId());
        assertEquals(nombre, tipoPrestamo.getNombre());
    }
}
