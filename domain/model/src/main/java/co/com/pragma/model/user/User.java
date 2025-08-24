package co.com.pragma.model.user;
import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.math.RoundingMode;
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
    private Role role;
    private BigDecimal baseSalary;
    private String phone;
    private String address;
    private LocalDate birthDate;

    public void trimFields(){
        this.name = this.name != null ? this.name.trim() : null;
        this.lastName = this.lastName != null ? this.lastName.trim() : null;
        this.email = this.email != null ? this.email.trim() : null;
        this.idNumber = this.idNumber != null ? this.idNumber.trim() : null;
        this.phone = this.phone != null ? this.phone.trim() : null;
        this.address = this.address != null ? this.address.trim() : null;
        if (this.baseSalary != null) {
            this.baseSalary = this.baseSalary.setScale(2, RoundingMode.HALF_UP);
        }
    }

    @Override
    public String toString() {
        return "User{" +
                "userId=" + userId +
                ", name='" + name + '\'' +
                ", lastName='" + lastName + '\'' +
                ", email='" + email + '\'' +
                ", idNumber='" + idNumber + '\'' +
                ", role=" + role.toString() +
                ", baseSalary=" + baseSalary +
                ", phone='" + phone + '\'' +
                ", address='" + address + '\'' +
                ", birthDate=" + birthDate +
                '}';
    }
}
