package co.com.pragma.r2dbc.entity;

import org.springframework.data.annotation.Id;
import lombok.*;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Table("Rol")
@Builder
public class RoleEntity {
    @Id
    @Column("UniqueID")
    private Integer rolId;

    @Column("nombre")
    private String name;

    @Column("descripcion")
    private String description;
}
