package co.com.pragma.api.config;

import co.com.pragma.api.Handler;
import co.com.pragma.api.constants.ApiConstants;
import co.com.pragma.api.dto.ErrorDTO;
import co.com.pragma.api.dto.UserRequestDTO;
import co.com.pragma.api.dto.UserResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
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
public class UserDocumentationConfig {
    @Bean
    @RouterOperations({
            @RouterOperation(
                    path = ApiConstants.ApiPaths.USERS_PATH,
                    produces = {MediaType.APPLICATION_JSON_VALUE},
                    method = RequestMethod.POST,
                    beanClass = Handler.class,
                    beanMethod = "listenPOSTSaveUserUseCase",
                    operation = @Operation(
                            operationId = ApiConstants.Operations.SAVE_USER_OPERATION_ID,
                            requestBody = @RequestBody(
                                    content = @Content(schema = @Schema(implementation = UserRequestDTO.class)),
                                    required = true,
                                    description = ApiConstants.Operations.SAVE_USER_REQUEST_BODY_DESC
                            ),
                            responses = {
                                    @ApiResponse(
                                            responseCode = ApiConstants.Responses.SUCCESS_CREATED_CODE,
                                            description = ApiConstants.Responses.SAVE_USER_SUCCESS_CREATED_DESC,
                                            content = @Content(schema = @Schema(implementation = UserResponseDTO.class))
                                    ),
                                    @ApiResponse(
                                            responseCode = ApiConstants.Responses.BAD_REQUEST_CODE,
                                            description = ApiConstants.Responses.SAVE_USER_BAD_REQUEST_DESC,
                                            content = @Content(schema = @Schema(implementation = ErrorDTO.class))
                                    ),
                                    @ApiResponse(
                                            responseCode = ApiConstants.Responses.CONFLICT_CODE,
                                            description = ApiConstants.Responses.SAVE_USER_CONFLICT_DESC,
                                            content = @Content(schema = @Schema(implementation = ErrorDTO.class))
                                    )
                            }
                    )
            ),
            @RouterOperation(
                    path = ApiConstants.ApiPaths.USER_BY_ID_NUMBER_PATH,
                    produces = {MediaType.APPLICATION_JSON_VALUE},
                    method = RequestMethod.GET,
                    beanClass = Handler.class,
                    beanMethod = "listenGETUserByIdNumberUseCase",
                    operation = @Operation(
                            operationId = ApiConstants.Operations.FIND_USER_BY_ID_NUMBER_OPERATION_ID,
                            parameters = @Parameter(
                                    in = ParameterIn.PATH,
                                    name = ApiConstants.ApiParams.ID_NUMBER_PARAM,
                                    description = ApiConstants.User.DESCRIPTION_ID_NUMBER,
                                    required = true,
                                    example = ApiConstants.User.EXAMPLE_ID_NUMBER
                            ),
                            responses = {
                                    @ApiResponse(
                                            responseCode = ApiConstants.Responses.SUCCESS_OK_CODE,
                                            description = ApiConstants.Responses.FIND_USER_SUCCESS_DESC,
                                            content = @Content(schema = @Schema(implementation = UserResponseDTO.class))
                                    ),
                                    @ApiResponse(
                                            responseCode = ApiConstants.Responses.NOT_FOUND_CODE,
                                            description = ApiConstants.Responses.FIND_USER_BY_ID_NUMBER_NOT_FOUND_DESC,
                                            content = @Content(schema = @Schema(implementation = ErrorDTO.class))
                                    )
                            }
                    )
            )
    })
    public RouterFunction<ServerResponse> userDocumentationRoutes() {
        return RouterFunctions.route(GET("/dummy-user-doc-route"), req -> ServerResponse.ok().build());
    }
}