package co.com.pragma.api.dto;

import co.com.pragma.model.user.Role;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "User Request", description = "User Requested Data")
public class UserDTO {
    @NotBlank(message = "Name can't be empty")
    @Size(min = 1, max = 50, message = "Name must be between 1 and 50 characters")
    private String name;

    @NotBlank(message = "Last name can't be empty")
    @Size(min = 1, max = 50, message = "Last name must be between 1 and 50 characters")
    private String lastName;

    @NotBlank(message = "Email can't be empty")
    @Size(min = 1, max = 100, message = "Email must be between 1 and 100 characters")
    @Email(message = "Email should be valid")
    private String email;

    @NotBlank(message = "Id Number can't be empty")
    @Size(min = 1,max = 50, message = "Id Number must be between 1 and 50 characters")
    private String idNumber;


    @NotNull(message = "Base Salary can't be empty")
    @Size(min = 1, max = 15000000, message = "Base Salary must be between 1 and 15.000.000 characters")
    private BigDecimal baseSalary;

    private Integer rolId;

    @Size(max = 20, message = "Phone must have less than 20 characters")
    private String phone;
    @Size(max = 255, message = "Address must have less than 255 characters")
    private String address;
    private LocalDate birthDate;
}