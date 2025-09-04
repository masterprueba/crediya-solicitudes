package co.com.crediya.solicitudes.api;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RequestPredicates.POST;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

import org.springdoc.core.annotations.RouterOperation;
import org.springdoc.core.annotations.RouterOperations;


@Configuration
public class RouterRest {
    @Bean
    @RouterOperations({
            @RouterOperation(
                    path = "/solicitud",
                    method = RequestMethod.POST,
                    beanClass = CrearSolicitudHandler.class,
                    beanMethod = "crear"
            ),
            @RouterOperation(
                    path = "/solicitud/listar",
                    method = RequestMethod.GET,
                    beanClass = ListarSolicitudesHandler.class,
                    beanMethod = "listarSolicitudes"
            ),
    })
    public RouterFunction<ServerResponse> routerFunction(CrearSolicitudHandler crearSolicitudHandler,
                                                       ListarSolicitudesHandler listarSolicitudHandler) {
        return route(POST("/solicitud"), crearSolicitudHandler::crear)
                .andRoute(GET("/solicitud/listar"), listarSolicitudHandler::listarSolicitudes);
    }
}
