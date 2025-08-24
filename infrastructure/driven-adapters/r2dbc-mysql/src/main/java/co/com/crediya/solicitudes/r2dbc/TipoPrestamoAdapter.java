package co.com.crediya.solicitudes.r2dbc;

import java.util.UUID;

import org.reactivecommons.utils.ObjectMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.reactive.TransactionalOperator;

import co.com.crediya.solicitudes.model.solicitud.TipoPrestamo;
import co.com.crediya.solicitudes.model.solicitud.gateways.CatalogoPrestamoRepository;
import co.com.crediya.solicitudes.r2dbc.entity.TipoPrestamoEntity;
import co.com.crediya.solicitudes.r2dbc.helper.ReactiveAdapterOperations;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public class TipoPrestamoAdapter extends ReactiveAdapterOperations<
        TipoPrestamo,
        TipoPrestamoEntity,
        UUID,
        TipoPrestamoR2dbcRepository
        > implements CatalogoPrestamoRepository {

    public TipoPrestamoAdapter(TipoPrestamoR2dbcRepository repository, ObjectMapper mapper, TransactionalOperator transactionalOperator) {
        super(repository, mapper, d -> mapper.map(d, TipoPrestamo.class));
    }

    @Override
        public Mono<Boolean> esTipoValido(String tipoPrestamo) {
            Flux<TipoPrestamoEntity> tipoPrestamoEntity = this.repository.findAll();
            return tipoPrestamoEntity.filter(tp -> tp.getNombre().equals(tipoPrestamo))
            .hasElements();
        }

}
