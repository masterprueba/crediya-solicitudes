package co.com.crediya.solicitudes.model.solicitud.gateways;

import reactor.core.publisher.Mono;
import java.util.UUID;

public interface CatalogoPrestamoRepository {
    Mono<Boolean> esTipoValido(String tipoPrestamo);
    Mono<UUID> obtenerIdPorNombre(String nombre);
}
