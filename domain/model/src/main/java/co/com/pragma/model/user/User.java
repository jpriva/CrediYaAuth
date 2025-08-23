package co.com.pragma.model.user;
import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class User {
    private String userId;
    private String name;
    private String lastName;
    private String email;
    private String idNumber;
    private String phone;
    private Rol rol;
    private String baseSalary;
}
