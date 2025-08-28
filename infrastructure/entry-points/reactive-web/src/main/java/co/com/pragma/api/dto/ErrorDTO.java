package co.com.pragma.api.dto;

import co.com.pragma.api.constants.ApiConstants;
import co.com.pragma.model.constants.ErrorMessage;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(name = "Error Response", description = "Error Response")
public class ErrorDTO {

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone = "UTC")
    @Schema(description = "Timestamp", example = "2025-01-01T00:00:00.000Z")
    private Instant timestamp;

    @Schema(description = "Error Path", example = ApiConstants.ApiPaths.USERS_PATH)
    private String path;

    @Schema(description = "Error Code", example = ErrorMessage.EMAIL_FORMAT_CODE)
    private String code;

    @Schema(description = "Error Message", example = ErrorMessage.EMAIL_FORMAT)
    private String message;
}
