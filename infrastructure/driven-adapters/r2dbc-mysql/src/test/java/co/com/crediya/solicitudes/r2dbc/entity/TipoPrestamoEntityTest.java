package co.com.crediya.solicitudes.r2dbc.entity;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class TipoPrestamoEntityTest {

    @Test
    void gettersAndSettersShouldWork() {
        TipoPrestamoEntity entity = new TipoPrestamoEntity();
        String id = "TP-01";
        String nombre = "Crédito Libre Inversión";

        entity.setId(id);
        entity.setNombre(nombre);

        assertThat(entity.getId()).isEqualTo(id);
        assertThat(entity.getNombre()).isEqualTo(nombre);
    }
}