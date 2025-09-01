 package co.com.crediya.solicitudes.api.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("OpenApi Config Test")
class OpenApiConfigTest {

    @Test
    @DisplayName("Debe crear OpenAPI con la información esperada")
    void customOpenAPI_creaInfoCorrecta() {
        // Arrange
        OpenApiConfig config = new OpenApiConfig();

        // Act
        OpenAPI openAPI = config.customOpenAPI();
        Info info = openAPI.getInfo();
        Contact contact = info.getContact();

        // Assert
        assertThat(info.getTitle()).isEqualTo("API de Solicitudes de Crédito Crediya");
        assertThat(info.getVersion()).isEqualTo("1.0.0");
        assertThat(info.getDescription()).contains("registro de solicitudes de crédito");
        assertThat(contact.getName()).isEqualTo("Equipo de Desarrollo Crediya");
        assertThat(contact.getEmail()).isEqualTo("jors.castro@gmail.com");
    }
}