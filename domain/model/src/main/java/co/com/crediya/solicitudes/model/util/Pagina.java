package co.com.crediya.solicitudes.model.util;

import java.util.List;

public record Pagina<T>(
        List<T> contenido,
        int page,
        int size,
        long totalElements,
        int totalPages,
        boolean hasNext
) {
}
