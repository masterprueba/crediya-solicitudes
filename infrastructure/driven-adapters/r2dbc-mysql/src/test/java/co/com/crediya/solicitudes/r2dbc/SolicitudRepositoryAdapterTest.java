package co.com.crediya.solicitudes.r2dbc;

import co.com.crediya.solicitudes.model.solicitud.Estado;
import co.com.crediya.solicitudes.model.solicitud.Solicitud;
import co.com.crediya.solicitudes.r2dbc.entity.SolicitudEntity;
import co.com.crediya.solicitudes.r2dbc.mapper.SolicitudMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SolicitudRepositoryAdapterTest {

    @Mock private SolicitudR2dbcRepository repository;
    @Mock private SolicitudMapper mapper;
    @Mock private TransactionalOperator tx;

    @InjectMocks private SolicitudRepositoryAdapter adapter;

    @BeforeEach
    void setup() {
        when(tx.transactional(any(Mono.class))).thenAnswer(inv -> inv.getArgument(0));
    }

    @Test
    void save_ok() {
        var s = Solicitud.builder()
                .id(UUID.randomUUID())
                .email("a@b.com")
                .monto(new BigDecimal("1000"))
                .plazoMeses(12)
                .estado(Estado.PENDIENTE_REVISION)
                .created(Instant.now())
                .build();
        var e = new SolicitudEntity();
        when(mapper.toEntity(s)).thenReturn(e);
        when(repository.insert(e)).thenReturn(Mono.empty());

        StepVerifier.create(adapter.save(s))
                .expectNext(s)
                .verifyComplete();
    }

    @Test
    void save_errorPropaga() {
        var s = Solicitud.builder().id(UUID.randomUUID()).email("a@b.com").monto(new BigDecimal("1")).plazoMeses(1).estado(Estado.PENDIENTE_REVISION).created(Instant.now()).build();
        var e = new SolicitudEntity();
        when(mapper.toEntity(s)).thenReturn(e);
        when(repository.insert(e)).thenReturn(Mono.error(new RuntimeException("db_error")));

        StepVerifier.create(adapter.save(s))
                .expectErrorMatches(ex -> ex.getMessage().contains("db_error"))
                .verify();
    }
}
