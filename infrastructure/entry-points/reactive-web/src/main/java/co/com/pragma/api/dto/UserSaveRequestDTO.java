package co.com.pragma.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
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
@Schema(name = "User Save Request", description = "User Requested Data for Save.")
public class UserSaveRequestDTO {
    @NotBlank(message = "Name can't be empty")
    @Size(max = 50, message = "Name must have less than 50 characters")
    @Schema(description = "User's first name", example = "John")
    private String name;

    @NotBlank(message = "Last name can't be empty")
    @Size(max = 50, message = "Last name must have less than 50 characters")
    @Schema(description = "User's last name", example = "Doe")
    private String lastName;

    @NotBlank(message = "Email can't be empty")
    @Email(message = "Email should be valid")
    @Size(max = 100, message = "Email must have less than 100 characters")
    @Schema(description = "User's unique email address", example = "john.doe@example.com")
    private String email;

    @Size(max = 50, message = "Id Number must have less than 50 characters")
    @Schema(description = "User's identification number", example = "123456789")
    private String idNumber;


    @NotNull(message = "Base Salary can't be empty")
    @DecimalMin(value = "1.00", message = "Base Salary must be at least 1")
    @DecimalMax(value = "15000000.00", message = "Base Salary must be less than 15,000,000")
    @Schema(description = "User's base salary", example = "5000000")
    private BigDecimal baseSalary;

    @NotNull(message = "Role ID can't be null")
    @Schema(description = "ID of the role to be assigned to the user", example = "3")
    private Integer rolId;

    @Size(max = 20, message = "Phone must have less than 20 characters")
    @Schema(description = "User's contact phone number", example = "3001234567")
    private String phone;
    @Size(max = 255, message = "Address must have less than 255 characters")
    @Schema(description = "User's home address", example = "Main St 123, Anytown")
    private String address;
    @Schema(description = "User's date of birth", example = "1990-01-15")
    private LocalDate birthDate;
}