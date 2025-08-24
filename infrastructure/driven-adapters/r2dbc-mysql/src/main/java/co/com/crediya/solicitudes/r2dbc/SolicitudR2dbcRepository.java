package co.com.crediya.solicitudes.r2dbc;

import co.com.crediya.solicitudes.r2dbc.entity.SolicitudEntity;
import org.springframework.data.repository.query.ReactiveQueryByExampleExecutor;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import java.util.UUID;

public interface SolicitudR2dbcRepository extends ReactiveCrudRepository<SolicitudEntity, UUID>, ReactiveQueryByExampleExecutor<SolicitudEntity> {

}
