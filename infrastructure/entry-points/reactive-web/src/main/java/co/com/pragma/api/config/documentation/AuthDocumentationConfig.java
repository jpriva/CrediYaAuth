package co.com.pragma.api.config.documentation;

import co.com.pragma.api.Handler;
import co.com.pragma.api.constants.ApiConstants;
import co.com.pragma.api.dto.ErrorDTO;
import co.com.pragma.api.dto.LoginRequestDTO;
import co.com.pragma.api.dto.LoginResponseDTO;
import co.com.pragma.model.constants.ErrorMessage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springdoc.core.annotations.RouterOperation;
import org.springdoc.core.annotations.RouterOperations;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;

@Configuration
public class AuthDocumentationConfig {
    @Bean
    @RouterOperations({
            @RouterOperation(
                    path = ApiConstants.ApiPaths.LOGIN_PATH,
                    produces = {MediaType.APPLICATION_JSON_VALUE},
                    method = RequestMethod.POST,
                    beanClass = Handler.class,
                    beanMethod = "listenPOSTLoginUseCase",
                    operation = @Operation(
                            operationId = ApiConstants.Operations.LOGIN_OPERATION_ID,
                            requestBody = @RequestBody(
                                    content = @Content(schema = @Schema(implementation = LoginRequestDTO.class)),
                                    required = true,
                                    description = ApiConstants.Operations.LOGIN_REQUEST_BODY_DESC
                            ),
                            responses = {
                                    @ApiResponse(
                                            responseCode = ApiConstants.Responses.SUCCESS_OK_CODE,
                                            description = ApiConstants.Responses.LOGIN_SUCCESS_DESC,
                                            content = @Content(schema = @Schema(implementation = LoginResponseDTO.class))
                                    ),
                                    @ApiResponse(
                                            responseCode = ApiConstants.Responses.BAD_REQUEST_CODE,
                                            description = ApiConstants.Responses.LOGIN_BAD_REQUEST_DESC,
                                            content = @Content(schema = @Schema(implementation = ErrorDTO.class))
                                    ),
                                    @ApiResponse(
                                            responseCode = ApiConstants.Responses.UNAUTHORIZED_CODE,
                                            description = ErrorMessage.INVALID_CREDENTIALS,
                                            content = @Content(schema = @Schema(implementation = ErrorDTO.class))
                                    )
                            }
                    )
            )
    })
    public RouterFunction<ServerResponse> authDocumentationRoutes() {
        return RouterFunctions.route(GET(ApiConstants.ApiPathMatchers.DUMMY_AUTH_DOC_ROUTE), req -> ServerResponse.ok().build());
    }
}
