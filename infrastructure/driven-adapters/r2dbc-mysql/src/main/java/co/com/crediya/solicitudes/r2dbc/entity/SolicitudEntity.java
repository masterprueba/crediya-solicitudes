package co.com.crediya.solicitudes.r2dbc.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.util.UUID;

@Table("solicitud")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SolicitudEntity {
    @Id
    private UUID id;
}
