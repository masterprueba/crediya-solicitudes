package co.com.crediya.solicitudes.api.error;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.reactive.error.DefaultErrorAttributes;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;

import co.com.crediya.solicitudes.model.solicitud.exceptions.DomainException;

@Component
public class GlobalErrorAttributes extends DefaultErrorAttributes{

    @Override
    public Map<String, Object> getErrorAttributes(ServerRequest request, ErrorAttributeOptions options) {
        Map<String, Object> errorAttributes = new LinkedHashMap<>();
        Throwable error = getError(request);

        if (error instanceof DomainException) {
            errorAttributes.put("status", HttpStatus.BAD_REQUEST.value());
            errorAttributes.put("code", "DATOS_INVALIDOS");
            errorAttributes.put("message", "La información proporcionada es inválida. " + error.getMessage());
        } else {
            errorAttributes.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
            errorAttributes.put("code", "ERROR_INTERNO");
            errorAttributes.put("message", "Ha ocurrido un error inesperado en el sistema. Por favor, contacte a soporte.");
        }

        errorAttributes.put("path", request.path());
        return errorAttributes;
    }

}
