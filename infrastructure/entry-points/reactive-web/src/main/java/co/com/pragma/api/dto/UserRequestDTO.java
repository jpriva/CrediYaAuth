package co.com.pragma.api.dto;

import co.com.pragma.api.constants.ApiConstants;
import com.fasterxml.jackson.annotation.JsonInclude;
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
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(name = "User Save Request", description = "User Requested Data for Save.")
public class UserRequestDTO {
    @NotBlank(message = ApiConstants.ValidationMessages.NAME_NOT_BLANK)
    @Size(max = 50, message = ApiConstants.ValidationMessages.NAME_SIZE)
    @Schema(description = ApiConstants.User.DESCRIPTION_NAME, example = ApiConstants.User.EXAMPLE_NAME)
    private String name;

    @NotBlank(message = ApiConstants.ValidationMessages.LAST_NAME_NOT_BLANK)
    @Size(max = 50, message = ApiConstants.ValidationMessages.LAST_NAME_SIZE)
    @Schema(description = ApiConstants.User.DESCRIPTION_LAST_NAME, example = ApiConstants.User.EXAMPLE_LAST_NAME)
    private String lastName;

    @NotBlank(message = ApiConstants.ValidationMessages.EMAIL_NOT_BLANK)
    @Email(message = ApiConstants.ValidationMessages.EMAIL_VALID)
    @Size(max = 100, message = ApiConstants.ValidationMessages.EMAIL_SIZE)
    @Schema(description = ApiConstants.User.DESCRIPTION_EMAIL, example = ApiConstants.User.EXAMPLE_EMAIL)
    private String email;

    @NotBlank(message = ApiConstants.ValidationMessages.ID_NUMBER_NOT_BLANK)
    @Size(max = 50, message = ApiConstants.ValidationMessages.ID_NUMBER_SIZE)
    @Schema(description = ApiConstants.User.DESCRIPTION_ID_NUMBER, example = ApiConstants.User.EXAMPLE_ID_NUMBER)
    private String idNumber;


    @NotNull(message = ApiConstants.ValidationMessages.SALARY_NOT_NULL)
    @DecimalMin(value = "1.00", message = ApiConstants.ValidationMessages.SALARY_MIN)
    @DecimalMax(value = "15000000.00", message = ApiConstants.ValidationMessages.SALARY_MAX)
    @Schema(description = ApiConstants.User.DESCRIPTION_BASE_SALARY, example = ApiConstants.User.EXAMPLE_BASE_SALARY)
    private BigDecimal baseSalary;

    @Schema(description = ApiConstants.Role.DESCRIPTION_ROLE_ID, example = ApiConstants.Role.EXAMPLE_ROLE_ID)
    private Integer rolId;

    @Size(max = 20, message = ApiConstants.ValidationMessages.PHONE_SIZE)
    @Schema(description = ApiConstants.User.DESCRIPTION_PHONE, example = ApiConstants.User.EXAMPLE_PHONE)
    private String phone;

    @Size(max = 255, message = ApiConstants.ValidationMessages.ADDRESS_SIZE)
    @Schema(description = ApiConstants.User.DESCRIPTION_ADDRESS, example = ApiConstants.User.EXAMPLE_ADDRESS)
    private String address;

    @Schema(description = ApiConstants.User.DESCRIPTION_BIRTH_DATE, example = ApiConstants.User.EXAMPLE_BIRTH_DATE)
    private LocalDate birthDate;

    @NotBlank(message = ApiConstants.ValidationMessages.PASSWORD_NOT_NULL)
    @Size(min = 8, message = ApiConstants.ValidationMessages.PASSWORD_SIZE)
    @Schema(description = ApiConstants.User.DESCRIPTION_PASSWORD, example = ApiConstants.User.EXAMPLE_PASSWORD)
    private String password;
}