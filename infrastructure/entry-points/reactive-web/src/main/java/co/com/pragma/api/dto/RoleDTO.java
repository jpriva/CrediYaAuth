package co.com.pragma.api.dto;

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
    @Schema(description = "Role's identifier", example = "3")
    private Integer rolId;
    @Schema(description = "Role's name", example = "CLIENTE")
    private String name;
    @Schema(description = "Role's description", example = "Cliente Solicitante")
    private String description;
}
