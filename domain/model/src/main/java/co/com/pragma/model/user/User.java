package co.com.pragma.model.user;
import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class User {
    private Integer userId;
    private String name;
    private String lastName;
    private String email;
    private String idNumber;
    private Rol rol;
    private BigDecimal baseSalary;
    private String phone;
    private String address;
    private LocalDate birthDate;
}
