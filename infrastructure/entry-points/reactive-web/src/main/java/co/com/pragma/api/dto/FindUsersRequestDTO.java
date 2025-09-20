package co.com.pragma.api.dto;

import co.com.pragma.api.constants.ApiConstants;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(name = ApiConstants.FindByExampleRequest.NAME_DTO, description = ApiConstants.FindByExampleRequest.DESCRIPTION_DTO)
public class FindUsersRequestDTO {

    @Schema(description = ApiConstants.User.DESCRIPTION_ID_NUMBER, example = ApiConstants.User.EXAMPLE_ID_NUMBER)
    private String idNumber;

    @Schema(description = ApiConstants.User.DESCRIPTION_EMAIL, example = ApiConstants.User.EXAMPLE_EMAIL)
    private String email;

    @Schema(description = ApiConstants.User.DESCRIPTION_NAME, example = ApiConstants.User.EXAMPLE_NAME)
    private String name;

    @Schema(description = ApiConstants.User.DESCRIPTION_SALARY_LOWER_THAN, example = "6000000")
    private BigDecimal salaryLowerThan;

    @Schema(description = ApiConstants.User.DESCRIPTION_SALARY_GREATER_THAN, example = "2000000")
    private BigDecimal salaryGreaterThan;
}
