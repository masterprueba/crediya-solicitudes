package co.com.crediya.solicitudes.api;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.reactive.server.WebTestClient;
import co.com.crediya.solicitudes.usecase.crearsolicitud.CrearSolicitudUseCase;

@ContextConfiguration(classes = {RouterRest.class, Handler.class, CrearSolicitudHandler.class})
@WebFluxTest
class RouterRestTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private CrearSolicitudUseCase crearSolicitudUseCase;

    @Test
    void contextLoads() {
        // Test básico para verificar que el contexto se carga correctamente
    }
}
