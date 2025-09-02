package co.com.pragma.model.user;

import co.com.pragma.model.role.Role;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class User {
    private Integer userId;
    private String name;
    private String lastName;
    private String email;
    private String idNumber;
    private Role role;
    private BigDecimal baseSalary;
    private String phone;
    private String address;
    private LocalDate birthDate;
    private String password;
}
