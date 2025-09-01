package co.com.crediya.solicitudes.model.cliente;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder(toBuilder = true)
public class ClienteToken {
    private String userId;
    private String email;
    private String role;
    private String token;
}
