package co.com.crediya.solicitudes.r2dbc;

import co.com.crediya.solicitudes.r2dbc.entity.SolicitudEntity;
import org.springframework.data.repository.query.ReactiveQueryByExampleExecutor;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import java.util.UUID;

// TODO: This file is just an example, you should delete or modify it
public interface MyReactiveRepository extends ReactiveCrudRepository<SolicitudEntity, UUID>, ReactiveQueryByExampleExecutor<SolicitudEntity> {

}
