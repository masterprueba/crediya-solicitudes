package co.com.crediya.solicitudes.api.error;

import co.com.crediya.solicitudes.model.exceptions.DomainException;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.web.WebProperties;
import org.springframework.context.support.StaticApplicationContext;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.web.reactive.function.server.HandlerStrategies;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.junit.jupiter.api.Assertions.*;

class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler buildHandler() {
        return new GlobalExceptionHandler(new GlobalErrorAttributes(), new WebProperties(), new StaticApplicationContext(), ServerCodecConfigurer.create());
    }

    private ServerResponse invoke(GlobalExceptionHandler geh, Throwable error) {
        var exchange = MockServerWebExchange.from(MockServerHttpRequest.get("/api/v1/solicitud").build());
        exchange.getAttributes().put("org.springframework.boot.web.reactive.error.DefaultErrorAttributes.ERROR", error);
        ServerRequest request = ServerRequest.create(exchange, HandlerStrategies.withDefaults().messageReaders());
        var rf = geh.getRoutingFunction(new GlobalErrorAttributes());
        var hf = rf.route(request).block();
        assertNotNull(hf);
        return hf.handle(request).block();
    }

    @Test
    void domainException_devuelve400() {
        var resp = invoke(buildHandler(), new DomainException("cliente_no_existe"));
        assertNotNull(resp);
        assertEquals(400, resp.statusCode().value());
    }

    @Test
    void genericException_devuelve500() {
        var resp = invoke(buildHandler(), new RuntimeException("boom"));
        assertNotNull(resp);
        assertEquals(500, resp.statusCode().value());
    }
}
