package co.com.pragma.api.dto;

import co.com.pragma.usecase.user.constants.ErrorMessage;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(name = "Error Response", description = "Error Response")
public class ErrorDTO {
    @Schema(description = "Error Code", example = ErrorMessage.EMAIL_FORMAT_CODE)
    private String code;
    @Schema(description = "Error Message", example = ErrorMessage.EMAIL_FORMAT)
    private String errorMessage;
}
