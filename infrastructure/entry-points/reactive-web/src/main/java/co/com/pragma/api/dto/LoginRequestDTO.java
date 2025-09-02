package co.com.pragma.api.dto;

import co.com.pragma.api.constants.ApiConstants;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(name = "Login Request", description = "Login Request")
public class LoginRequestDTO {
    @Schema(description = ApiConstants.User.DESCRIPTION_EMAIL, example = ApiConstants.User.EXAMPLE_EMAIL)
    private String email;
    @Schema(description = ApiConstants.User.DESCRIPTION_PASSWORD, example = ApiConstants.User.EXAMPLE_PASSWORD)
    private String password;
}
