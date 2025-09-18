package co.com.crediya.solicitudes.r2dbc;

import co.com.crediya.solicitudes.model.exceptions.DomainException;
import co.com.crediya.solicitudes.model.solicitud.PrestamoActivo;
import co.com.crediya.solicitudes.model.solicitud.Solicitud;
import co.com.crediya.solicitudes.model.solicitud.gateways.SolicitudRepository;
import co.com.crediya.solicitudes.r2dbc.entity.SolicitudEntity;
import co.com.crediya.solicitudes.r2dbc.mapper.SolicitudMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Repository
@Slf4j
@RequiredArgsConstructor
public class SolicitudRepositoryAdapter implements SolicitudRepository {

    private final SolicitudR2dbcRepository repository;
    private final SolicitudMapper solicitudMapper;
    private final TransactionalOperator tx;
    private final DatabaseClient db;

    @Override
    public Mono<Solicitud> findById(String id) {
        return repository.findById(id)
                .switchIfEmpty(Mono.error(new DomainException("Solicitud no encontrada con ID: " + id)))
                .map(solicitudMapper::toDomain)
                .doOnSuccess(solicitud -> log.info("Solicitud encontrada con éxito. ID: {}", solicitud.getId()))
                .doOnError(error -> log.error("Error al buscar la solicitud con ID: {}", id, error));

    }

    @Override
    public Mono<Solicitud> save(Solicitud solicitud) {
        return Mono.just(solicitud)
                .map(this::asegurarId) // Generar UUID si no tiene ID
                .flatMap(solicitudConId -> {
                    SolicitudEntity entity = solicitudMapper.toEntity(solicitudConId);
                    return repository.insert(entity)
                            .then(Mono.just(solicitudConId)); // Retornar la solicitud con ID generado
                })
                .as(tx::transactional)
                .doOnSuccess(saved -> log.info("Solicitud insertada con éxito. ID: {}", saved.getId()))
                .doOnError(error -> log.error("Error al insertar la solicitud", error));
    }
    
    private Solicitud asegurarId(Solicitud solicitud) {
        if (solicitud.getId() == null) {
            return solicitud.toBuilder()
                    .id(UUID.randomUUID())
                    .build();
        }
        return solicitud;
    }

    @Override
    public Mono<Solicitud> update(Solicitud solicitud) {
        return Mono.just(solicitud)
                .map(solicitudMapper::toEntity)
                .flatMap(repository::save)
                .thenReturn(solicitud) // Al completarse el insert, devuelve el objeto Solicitud original
                .as(tx::transactional)
                .doOnSuccess(saved -> log.info("Solicitud actulizar estado con éxito. ID: {}, estado: {}", saved.getId(), saved.getEstado()))
                .doOnError(error -> log.error("Error al actualizar estado de la solicitud", error));
    }

    @Override
    public Flux<PrestamoActivo> findPrestamosActivosByEmail(String email) {
        String sql = "SELECT s.monto, s.plazo, tp.tasa_interes " +
                     "FROM solicitud s " +
                     "JOIN tipo_prestamo tp ON s.id_tipo_prestamo = tp.id_tipo_prestamo " +
                     "WHERE s.email = :email AND s.id_estado = 2";

        return db.sql(sql)
                .bind("email", email)
                .map((row, metadata) -> PrestamoActivo.builder()
                        .monto(row.get("monto", java.math.BigDecimal.class))
                        .plazoMeses(row.get("plazo", Integer.class))
                        .tasaAnualPct(row.get("tasa_interes", Double.class))
                        .build())
                .all()
                .doOnNext(prestamo -> log.debug("Préstamo activo encontrado para {}: Monto: {}, Plazo: {} meses",
                        email, prestamo.getMonto(), prestamo.getPlazoMeses()))
                .doOnError(error -> log.error("Error al buscar préstamos activos para email: {}", email, error));
    }
}
