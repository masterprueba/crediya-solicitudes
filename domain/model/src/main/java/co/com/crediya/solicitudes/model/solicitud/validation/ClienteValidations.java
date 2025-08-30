package co.com.crediya.solicitudes.model.solicitud.validation;

import co.com.crediya.solicitudes.model.exceptions.DomainException;
import co.com.crediya.solicitudes.model.solicitud.Solicitud;
import reactor.core.publisher.Mono;

public class ClienteValidations {
    private ClienteValidations() {
        throw new IllegalStateException("Cliente class");
    }

    public static ClienteValidation validarClienteYPermisos() {
        return clienteToken -> {
            if (clienteToken == null) {
                return Mono.error(new DomainException("usuario_no_autenticado"));
            }
            return Mono.just(clienteToken);
        };
    }

    public static ClienteValidation validarSoloClientes() {
        return clienteToken -> {
            if (!"CLIENTE".equalsIgnoreCase(clienteToken.getRole())) {
                return Mono.error(new DomainException("usuario_no_autorizado"));
            }
            return Mono.just(clienteToken);
        };
    }

    public static ClienteValidation validarClienteCreaSolicitudPropia(Solicitud solicitud) {
        return clienteToken -> {
            if (!clienteToken.getEmail().equalsIgnoreCase(solicitud.getEmail())) {
                return Mono.error(new DomainException("cliente_no_autorizado_para_esta_solicitud"));
            }
            return Mono.just(clienteToken);
        };
    }

    public static ClienteValidation completa(Solicitud solicitud) {
        return validarClienteYPermisos().and(validarSoloClientes()).and(validarClienteCreaSolicitudPropia(solicitud));
    }
}
