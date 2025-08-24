package co.com.pragma.model.user;

import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.StringJoiner;

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

    public void trimFields() {
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
        StringJoiner joiner = new java.util.StringJoiner(", ", "User{", "}");
        joiner.add("userId=" + this.userId);
        joiner.add("name='" + this.name + "'");
        joiner.add("lastName='" + this.lastName + "'");
        joiner.add("email='" + this.email + "'");
        joiner.add("role=");
        joiner.add(this.role != null ? this.role.toString() : "null");
        joiner.add("idNumber='***'");
        joiner.add("baseSalary='***'");
        joiner.add("phone='***'");
        joiner.add("address='***'");
        joiner.add("birthDate='***'");

        return joiner.toString();
    }
}
