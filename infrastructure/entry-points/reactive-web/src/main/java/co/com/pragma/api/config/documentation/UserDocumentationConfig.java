package co.com.pragma.api.config.documentation;

import co.com.pragma.api.Handler;
import co.com.pragma.api.constants.ApiConstants;
import co.com.pragma.api.dto.ErrorDTO;
import co.com.pragma.api.dto.FindUsersRequestDTO;
import co.com.pragma.api.dto.UserRequestDTO;
import co.com.pragma.api.dto.UserResponseDTO;
import co.com.pragma.model.constants.ErrorMessage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.media.ArraySchema;
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
                            summary = ApiConstants.Operations.SAVE_USER_SUMMARY,
                            description = ApiConstants.Operations.SAVE_USER_DESCRIPTION,
                            security = @SecurityRequirement(name = ApiConstants.ApiConfig.NAME_BEARER_AUTH),
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
                            summary = ApiConstants.Operations.FIND_USER_BY_ID_NUMBER_SUMMARY,
                            description = ApiConstants.Operations.FIND_USER_BY_ID_NUMBER_DESCRIPTION,
                            security = @SecurityRequirement(name = ApiConstants.ApiConfig.NAME_BEARER_AUTH),
                            parameters = {
                                    @Parameter(
                                            in = ParameterIn.PATH,
                                            name = ApiConstants.ApiParams.ID_NUMBER_PARAM,
                                            description = ApiConstants.User.DESCRIPTION_ID_NUMBER,
                                            required = true,
                                            example = ApiConstants.User.EXAMPLE_ID_NUMBER)
                            },
                            responses = {
                                    @ApiResponse(
                                            responseCode = ApiConstants.Responses.SUCCESS_OK_CODE,
                                            description = ApiConstants.Responses.FIND_USER_SUCCESS_DESC,
                                            content = @Content(schema = @Schema(implementation = UserResponseDTO.class))
                                    ),
                                    @ApiResponse(
                                            responseCode = ApiConstants.Responses.NOT_FOUND_CODE,
                                            description = ErrorMessage.USER_NOT_FOUND,
                                            content = @Content(schema = @Schema(implementation = ErrorDTO.class))
                                    )
                            }
                    )
            ),
            @RouterOperation(
                    path = ApiConstants.ApiPaths.USERS_BY_EMAIL_PATH,
                    produces = {MediaType.APPLICATION_JSON_VALUE},
                    method = RequestMethod.POST,
                    beanClass = Handler.class,
                    beanMethod = "listenPOSTUsersByEmailUseCase",
                    operation = @Operation(
                            operationId = ApiConstants.Operations.FIND_USERS_BY_EMAIL_OPERATION_ID,
                            summary = ApiConstants.Operations.FIND_USERS_BY_EMAIL_SUMMARY,
                            description = ApiConstants.Operations.FIND_USERS_BY_EMAIL_DESCRIPTION,
                            security = @SecurityRequirement(name = ApiConstants.ApiConfig.NAME_BEARER_AUTH),
                            requestBody = @RequestBody(
                                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = String.class, example = "user1@example.com"))),
                                    required = true,
                                    description = ApiConstants.Operations.FIND_USERS_BY_EMAIL_REQUEST_BODY_DESC
                            ),
                            responses = {
                                    @ApiResponse(responseCode = ApiConstants.Responses.SUCCESS_OK_CODE, description = ApiConstants.Responses.FIND_USERS_BY_EMAIL_SUCCESS_DESC,
                                            content = @Content(array = @ArraySchema(schema = @Schema(implementation = UserResponseDTO.class)))
                                    ),
                                    @ApiResponse(responseCode = ApiConstants.Responses.BAD_REQUEST_CODE, description = ApiConstants.Responses.BAD_REQUEST_DESC, content = @Content(schema = @Schema(implementation = ErrorDTO.class)))
                            }
                    )
            ),
            @RouterOperation(
                    path = ApiConstants.ApiPaths.USER_EMAILS_BY_FILTER_PATH,
                    produces = {MediaType.APPLICATION_JSON_VALUE},
                    method = RequestMethod.POST,
                    beanClass = Handler.class,
                    beanMethod = "listenPOSTFindUserEmailsByFilterUseCase",
                    operation = @Operation(
                            operationId = ApiConstants.Operations.FIND_USER_EMAILS_BY_FILTER_OPERATION_ID,
                            summary = ApiConstants.Operations.FIND_USER_EMAILS_BY_FILTER_SUMMARY,
                            description = ApiConstants.Operations.FIND_USER_EMAILS_BY_FILTER_DESCRIPTION,
                            security = @SecurityRequirement(name = ApiConstants.ApiConfig.NAME_BEARER_AUTH),
                            requestBody = @RequestBody(
                                    content = @Content(schema = @Schema(implementation = FindUsersRequestDTO.class)),
                                    required = true,
                                    description = ApiConstants.Operations.FIND_USER_EMAILS_BY_FILTER_REQUEST_BODY_DESC
                            ),
                            responses = {
                                    @ApiResponse(responseCode = ApiConstants.Responses.SUCCESS_OK_CODE, description = ApiConstants.Responses.FIND_USER_EMAILS_BY_FILTER_SUCCESS_DESC,
                                            content = @Content(array = @ArraySchema(schema = @Schema(implementation = String.class, example = "user.found@example.com")))
                                    ),
                                    @ApiResponse(responseCode = ApiConstants.Responses.BAD_REQUEST_CODE, description = ApiConstants.Responses.BAD_REQUEST_DESC, content = @Content(schema = @Schema(implementation = ErrorDTO.class)))
                            }
                    )
            )
    })
    public RouterFunction<ServerResponse> userDocumentationRoutes() {
        return RouterFunctions.route(GET(ApiConstants.ApiPathMatchers.DUMMY_USER_DOC_ROUTE), req -> ServerResponse.ok().build());
    }
}