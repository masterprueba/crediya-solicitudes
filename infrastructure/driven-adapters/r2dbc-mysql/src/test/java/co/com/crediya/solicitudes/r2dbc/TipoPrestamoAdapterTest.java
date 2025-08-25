package co.com.crediya.solicitudes.r2dbc;

import co.com.crediya.solicitudes.r2dbc.entity.TipoPrestamoEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.reactivecommons.utils.ObjectMapper;
import org.reactivecommons.utils.ObjectMapperImp;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.UUID;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TipoPrestamoAdapterTest {

    @Mock
    private TipoPrestamoR2dbcRepository repository;

    private TipoPrestamoAdapter adapter;

    @BeforeEach
    void setUp() {
        ObjectMapper mapper = new ObjectMapperImp();
        adapter = new TipoPrestamoAdapter(repository, mapper);
    }

    @Test
    void esTipoValido_true_y_false() {
        when(repository.findAll()).thenReturn(Flux.just(
                entity("1", "LIBRE_INVERSION"),
                entity("2", "EDUCATIVO")
        ));

        StepVerifier.create(adapter.esTipoValido("LIBRE_INVERSION"))
                .expectNext(true)
                .verifyComplete();

        StepVerifier.create(adapter.esTipoValido("HIPOTECARIO"))
                .expectNext(false)
                .verifyComplete();
    }

    @Test
    void obtenerIdPorNombre_ok() {
        when(repository.findAll()).thenReturn(Flux.just(
                entity(UUID.randomUUID().toString(), "EDUCATIVO"),
                entity("00000000-0000-0000-0000-000000000123", "LIBRE_INVERSION")
        ));

        StepVerifier.create(adapter.obtenerIdPorNombre("LIBRE_INVERSION"))
                .expectNext(UUID.fromString("00000000-0000-0000-0000-000000000123"))
                .verifyComplete();
    }

    private static TipoPrestamoEntity entity(String id, String nombre) {
        TipoPrestamoEntity e = new TipoPrestamoEntity();
        e.setId(id);
        e.setNombre(nombre);
        return e;
    }
}
