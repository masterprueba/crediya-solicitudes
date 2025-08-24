package co.com.crediya.solicitudes.r2dbc.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import lombok.Data;

import java.util.UUID;

@Table("tipo_prestamo")
@Data
public class TipoPrestamoEntity {
    @Id
    @Column("id_tipo_prestamo") private UUID id;
    @Column("nombre")               private String nombre;
    // getters/setters
}
