package co.com.crediya.solicitudes.r2dbc;

import co.com.crediya.solicitudes.r2dbc.entity.TipoPrestamoEntity;
import org.springframework.data.repository.query.ReactiveQueryByExampleExecutor;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;


public interface TipoPrestamoR2dbcRepository extends ReactiveCrudRepository<TipoPrestamoEntity, String>, 
ReactiveQueryByExampleExecutor<TipoPrestamoEntity> {
}
