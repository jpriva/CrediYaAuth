package co.com.pragma.r2dbc.entity;

import org.springframework.data.annotation.Id;
import lombok.*;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.time.LocalDate;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@Table("Usuario")
public class UserEntity {
    @Id
    @Column("id_usuario")
    private Integer userId;

    @Column("nombre")
    private String name;

    @Column("apellido")
    private String lastName;

    @Column("email")
    private String email;

    @Column("documento_identidad")
    private String idNumber;

    @Column("id_rol")
    private Integer rolId;

    @Column("salario_base")
    private BigDecimal baseSalary;

    @Column("telefono")
    private String phone;

    @Column("direccion")
    private String address;

    @Column("fecha_nacimiento")
    private LocalDate birthDate;
}
