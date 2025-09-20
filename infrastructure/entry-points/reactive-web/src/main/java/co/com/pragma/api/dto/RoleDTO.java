package co.com.pragma.api.dto;

import co.com.pragma.api.constants.ApiConstants;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(name = "Role", description = "Role Data.")
public class RoleDTO {
    @Schema(description = ApiConstants.Role.DESCRIPTION_ROLE_ID, example = ApiConstants.Role.EXAMPLE_ROLE_ID)
    private Integer rolId;
    @Schema(description = ApiConstants.Role.DESCRIPTION_ROLE_NAME, example = ApiConstants.Role.CLIENT_ROLE_NAME)
    private String name;
    @Schema(description = ApiConstants.Role.DESCRIPTION_ROLE_DESCRIPTION, example = ApiConstants.Role.EXAMPLE_ROLE_DESCRIPTION)
    private String description;
}
