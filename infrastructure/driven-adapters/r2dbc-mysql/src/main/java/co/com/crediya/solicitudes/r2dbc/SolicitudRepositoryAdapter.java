package co.com.crediya.solicitudes.r2dbc;

import co.com.crediya.solicitudes.model.solicitud.Solicitud;
import co.com.crediya.solicitudes.r2dbc.entity.SolicitudEntity;
import co.com.crediya.solicitudes.r2dbc.helper.ReactiveAdapterOperations;
import co.com.crediya.solicitudes.r2dbc.mapper.SolicitudMapper;
import lombok.extern.slf4j.Slf4j;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Slf4j
public class SolicitudRepositoryAdapter extends ReactiveAdapterOperations<
        Solicitud/* change for domain model */,
        SolicitudEntity/* change for adapter model */,
        UUID,
        SolicitudRepository
        > {

    private final TransactionalOperator tx;
    private final SolicitudMapper solicitudMapper;

    public SolicitudRepositoryAdapter(SolicitudRepository repository, ObjectMapper mapper, TransactionalOperator transactionalOperator, SolicitudMapper solicitudMapper) {
        /**
         *  Could be use mapper.mapBuilder if your domain model implement builder pattern
         *  super(repository, mapper, d -> mapper.mapBuilder(d,ObjectModel.ObjectModelBuilder.class).build());
         *  Or using mapper.map with the class of the object model
         */
        super(repository, mapper, d -> mapper.map(d, Solicitud.class/* change for domain model */));
        this.tx = transactionalOperator;
        this.solicitudMapper = solicitudMapper;
    }



    @Override
    public Mono<Solicitud> save(Solicitud s) {
        Mono<Solicitud> solicitudMono = Mono.just(s)
                .map(this::toData)
                .flatMap(repository::save)
                .map(solicitudMapper::toDomain);
        return solicitudMono.as(tx::transactional)
                .doOnSuccess(sd -> log.info("Solicitud guardada con exíto. {}",sd.getId()));
    }
}
