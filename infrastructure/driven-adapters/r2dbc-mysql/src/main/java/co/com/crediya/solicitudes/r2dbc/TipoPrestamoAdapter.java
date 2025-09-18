package co.com.crediya.solicitudes.r2dbc;

import java.util.UUID;

import org.reactivecommons.utils.ObjectMapper;
import org.springframework.stereotype.Repository;

import co.com.crediya.solicitudes.model.solicitud.TipoPrestamo;
import co.com.crediya.solicitudes.model.solicitud.gateways.CatalogoPrestamoRepository;
import co.com.crediya.solicitudes.r2dbc.entity.TipoPrestamoEntity;
import co.com.crediya.solicitudes.r2dbc.helper.ReactiveAdapterOperations;
import reactor.core.publisher.Mono;

@Repository
public class TipoPrestamoAdapter extends ReactiveAdapterOperations<
        TipoPrestamo,
        TipoPrestamoEntity,
        String,
        TipoPrestamoR2dbcRepository
        > implements CatalogoPrestamoRepository {

    public TipoPrestamoAdapter(TipoPrestamoR2dbcRepository repository, ObjectMapper mapper) {
        super(repository, mapper, d -> mapper.map(d, TipoPrestamo.class));
    }

    @Override
    public Mono<TipoPrestamo> obtenerTipoPrestamoPorNombre(String nombre) {
        return this.repository.findAll()
                .filter(tp -> tp.getNombre().equals(nombre))
                .next()
                .map(tp -> TipoPrestamo.builder()
                        .id(UUID.fromString(tp.getId()))
                        .nombre(tp.getNombre())
                        .tasaInteres(tp.getTasaInteres())
                        .montoMinimo(tp.getMontoMinimo())
                        .montoMaximo(tp.getMontoMaximo())
                        .validacionAutomatica(tp.getValidacionAutomatica())
                        .build());
    }

}
