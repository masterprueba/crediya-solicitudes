package co.com.crediya.solicitudes.r2dbc;

import co.com.crediya.solicitudes.r2dbc.entity.TipoPrestamoEntity;
import org.springframework.data.repository.query.ReactiveQueryByExampleExecutor;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import java.util.UUID;

public interface TipoPrestamoRepository extends ReactiveCrudRepository<TipoPrestamoEntity, UUID>, ReactiveQueryByExampleExecutor<TipoPrestamoEntity> {
}
