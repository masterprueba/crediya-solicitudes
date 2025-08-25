package co.com.crediya.solicitudes.api.error;

import co.com.crediya.solicitudes.model.exceptions.DomainException;
import org.junit.jupiter.api.Test;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.web.reactive.function.server.HandlerStrategies;
import org.springframework.web.reactive.function.server.ServerRequest;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class GlobalErrorAttributesTest {

    private final GlobalErrorAttributes attrs = new GlobalErrorAttributes();

    private ServerRequest requestWithError(Throwable t) {
        var exchange = MockServerWebExchange.from(MockServerHttpRequest.get("/api/v1/solicitud").build());
        exchange.getAttributes().put("org.springframework.boot.web.reactive.error.DefaultErrorAttributes.ERROR", t);
        return ServerRequest.create(exchange, HandlerStrategies.withDefaults().messageReaders());
    }

    @Test
    void domainException_devuelve400() {
        ServerRequest req = requestWithError(new DomainException("cliente_no_existe"));
        Map<String, Object> map = attrs.getErrorAttributes(req, ErrorAttributeOptions.defaults());
        assertEquals(400, map.get("status"));
        assertEquals("DATOS_INVALIDOS", map.get("code"));
        assertTrue(map.get("message").toString().contains("cliente_no_existe"));
    }

    @Test
    void exception_generica_devuelve500() {
        ServerRequest req = requestWithError(new RuntimeException("boom"));
        Map<String, Object> map = attrs.getErrorAttributes(req, ErrorAttributeOptions.defaults());
        assertEquals(500, map.get("status"));
        assertEquals("ERROR_INTERNO", map.get("code"));
    }
}
