package co.com.crediya.solicitudes.r2dbc;

import co.com.crediya.solicitudes.model.solicitud.Solicitud;
import co.com.crediya.solicitudes.model.solicitud.gateways.SolicitudRepository;
import co.com.crediya.solicitudes.r2dbc.mapper.SolicitudMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.core.publisher.Mono;

@Repository
@Slf4j
@RequiredArgsConstructor
public class SolicitudRepositoryAdapter implements SolicitudRepository {

    private final SolicitudR2dbcRepository repository;
    private final SolicitudMapper solicitudMapper;
    private final TransactionalOperator tx;

    @Override
    public Mono<Solicitud> save(Solicitud s) {
        return Mono.just(s)
                .map(solicitudMapper::toEntity)
                .flatMap(repository::insert)
                .thenReturn(s) // Al completarse el insert, devuelve el objeto Solicitud original
                .as(tx::transactional)
                .doOnSuccess(saved -> log.info("Solicitud insertada con éxito. ID: {}", saved.getId()))
                .doOnError(error -> log.error("Error al insertar la solicitud", error));
    }
}
