package co.com.crediya.solicitudes.r2dbc;

import co.com.crediya.solicitudes.model.solicitud.SolicitudResumen;
import org.springframework.data.repository.query.ReactiveQueryByExampleExecutor;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import java.util.UUID;

public interface SolicitudResumenR2dbcRepository extends ReactiveCrudRepository<SolicitudResumen, UUID>, ReactiveQueryByExampleExecutor<SolicitudResumen> {

}
