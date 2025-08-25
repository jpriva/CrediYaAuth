package co.com.pragma.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(name = "User Response", description = "User Data Response.")
public class UserResponseDTO {

    @Schema(description = "User's unique identifier.", example = "123")
    private Integer userId;

    @Schema(description = "User's first name", example = "John")
    private String name;

    @Schema(description = "User's last name", example = "Doe")
    private String lastName;

    @Schema(description = "User's unique email address", example = "john.doe@example.com")
    private String email;

    @Schema(description = "User's identification number", example = "123456789")
    private String idNumber;

    @Schema(description = "Role's identifier", example = "3")
    private Integer roleId;

    @Schema(description = "Role's name", example = "CLIENTE")
    private String roleName;

    @Schema(description = "Role's description", example = "Cliente Solicitante")
    private String roleDescription;

    @Schema(description = "User's base salary", example = "5000000")
    private BigDecimal baseSalary;

    @Schema(description = "User's contact phone number", example = "3001234567")
    private String phone;

    @Schema(description = "User's home address", example = "Main St 123, Anytown")
    private String address;

    @Schema(description = "User's date of birth", example = "1990-01-15")
    private LocalDate birthDate;
}