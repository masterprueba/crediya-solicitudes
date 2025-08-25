package co.com.crediya.solicitudes.api.config;

import co.com.crediya.solicitudes.api.CrearSolicitudHandler;
import co.com.crediya.solicitudes.api.RouterRest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.reactive.server.WebTestClient;

@ContextConfiguration(classes = {RouterRest.class, CrearSolicitudHandler.class})
@WebFluxTest
@Import({CorsConfig.class, SecurityHeadersConfig.class})
class ConfigTest {

    @Autowired
    private WebTestClient webTestClient;



}