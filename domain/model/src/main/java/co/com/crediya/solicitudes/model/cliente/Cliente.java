package co.com.crediya.solicitudes.model.cliente;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class Cliente {
    private String usuario;
    private String email;
    private String documento_identidad;
}
