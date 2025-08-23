package co.com.pragma.r2dbc.entity;

import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Table("Rol")
public class RolEntity {
    @Id
    @Column("UniqueID")
    private Integer rolId;

    @Column("nombre")
    private String name;

    @Column("descripcion")
    private String description;
}
