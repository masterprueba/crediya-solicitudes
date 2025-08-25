package co.com.crediya.solicitudes.r2dbc;

import co.com.crediya.solicitudes.r2dbc.entity.SolicitudEntity;
import org.springframework.data.r2dbc.repository.Modifying;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.query.ReactiveQueryByExampleExecutor;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

public interface SolicitudR2dbcRepository extends ReactiveCrudRepository<SolicitudEntity, String>, ReactiveQueryByExampleExecutor<SolicitudEntity> {

    @Modifying
    @Query("INSERT INTO solicitud (id_solicitud, email, monto, plazo, id_estado, id_tipo_prestamo, created) " +
           "VALUES (:#{#entity.id}, :#{#entity.email}, :#{#entity.monto}, " +
           ":#{#entity.plazoMeses}, :#{#entity.idEstado}, :#{#entity.tipoPrestamoId}, :#{#entity.created})")
    Mono<Integer> insert(@Param("entity") SolicitudEntity entity);

}
