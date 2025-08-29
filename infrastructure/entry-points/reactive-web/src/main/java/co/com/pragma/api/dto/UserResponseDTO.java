package co.com.pragma.api.dto;

import co.com.pragma.api.constants.ApiConstants;
import com.fasterxml.jackson.annotation.JsonInclude;
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
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(name = "User Response", description = "User Data Response.")
public class UserResponseDTO {

    @Schema(description = ApiConstants.User.DESCRIPTION_USER_ID, example = ApiConstants.User.EXAMPLE_USER_ID)
    private Integer userId;

    @Schema(description = ApiConstants.User.DESCRIPTION_NAME, example = ApiConstants.User.EXAMPLE_NAME)
    private String name;

    @Schema(description = ApiConstants.User.DESCRIPTION_LAST_NAME, example = ApiConstants.User.EXAMPLE_LAST_NAME)
    private String lastName;

    @Schema(description = ApiConstants.User.DESCRIPTION_EMAIL, example = ApiConstants.User.EXAMPLE_EMAIL)
    private String email;

    @Schema(description = ApiConstants.User.DESCRIPTION_ID_NUMBER, example = ApiConstants.User.EXAMPLE_ID_NUMBER)
    private String idNumber;

    @Schema(description = ApiConstants.Role.DESCRIPTION_ROLE)
    private RoleDTO role;

    @Schema(description = ApiConstants.User.DESCRIPTION_BASE_SALARY, example = ApiConstants.User.EXAMPLE_BASE_SALARY)
    private BigDecimal baseSalary;

    @Schema(description = ApiConstants.User.DESCRIPTION_PHONE, example = ApiConstants.User.EXAMPLE_PHONE)
    private String phone;

    @Schema(description = ApiConstants.User.DESCRIPTION_ADDRESS, example = ApiConstants.User.EXAMPLE_ADDRESS)
    private String address;

    @Schema(description = ApiConstants.User.DESCRIPTION_BIRTH_DATE, example = ApiConstants.User.EXAMPLE_BIRTH_DATE)
    private LocalDate birthDate;
}