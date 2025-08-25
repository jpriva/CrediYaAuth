package co.com.pragma.api.dto;

import co.com.pragma.model.user.exceptions.ErrorMessage;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
@Schema(name = "Error Response", description = "Error Response")
public class ErrorDTO {
    @Schema(description = "Error Code", example = ErrorMessage.EMAIL_FORMAT_CODE)
    private String code;
    @Schema(description = "Error Message", example = ErrorMessage.EMAIL_FORMAT)
    private String errorMessage;
}
