package co.com.crediya.solicitudes.model.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("Pagina Record Test")
class PaginaTest {

    @Test
    @DisplayName("Crear instancia de Pagina y verificar getters - Exitoso")
    void testPaginaRecord() {
        List<String> content = Collections.singletonList("test");
        int page = 0;
        int size = 1;
        long totalElements = 1L;
        int totalPages = 1;
        boolean hasNext = false;

        Pagina<String> pagina = new Pagina<>(content, page, size, totalElements, totalPages, hasNext);

        assertEquals(content, pagina.contenido());
        assertEquals(page, pagina.page());
        assertEquals(size, pagina.size());
        assertEquals(totalElements, pagina.totalElements());
        assertEquals(totalPages, pagina.totalPages());
        assertEquals(hasNext, pagina.hasNext());
    }
}
