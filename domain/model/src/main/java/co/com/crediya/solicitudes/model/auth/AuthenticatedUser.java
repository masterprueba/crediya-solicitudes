package co.com.crediya.solicitudes.model.auth;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Representa un usuario autenticado en el contexto de solicitudes
 */
@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class AuthenticatedUser {
    private String userId;
    private String email;
    private String role;
    private String token;
    
    public boolean isCliente() {
        return "CLIENTE".equals(role);
    }
    
    public boolean canCreateSolicitudFor(String email) {
        return isCliente() && this.email.equals(email);
    }
}
