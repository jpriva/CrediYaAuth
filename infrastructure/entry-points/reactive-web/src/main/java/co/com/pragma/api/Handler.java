package co.com.pragma.api;

import co.com.pragma.api.dto.ErrorDTO;
import co.com.pragma.api.dto.UserSaveRequestDTO;
import co.com.pragma.api.mapper.UserMapper;
import co.com.pragma.model.logs.gateways.LoggerPort;
import co.com.pragma.model.user.exceptions.EmailTakenException;
import co.com.pragma.model.user.constants.ErrorMessage;
import co.com.pragma.model.user.exceptions.UserException;
import co.com.pragma.usecase.user.SaveUserUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.server.ServerWebInputException;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class Handler {
    private final SaveUserUseCase userUseCase;
    private final LoggerPort logger;

    public Mono<ServerResponse> listenPOSTSaveUserUseCase(ServerRequest serverRequest) {
        return serverRequest.bodyToMono(UserSaveRequestDTO.class)
                .map(UserMapper::toDomain)
                .flatMap(userUseCase::execute)
                .map(UserMapper::toResponseDto)
                .flatMap(savedUser ->
                        ServerResponse.status(HttpStatus.CREATED)
                                .contentType(MediaType.APPLICATION_JSON)
                                .bodyValue(savedUser)
                ).onErrorResume(ServerWebInputException.class, ex -> {
                    logger.error("Error retrieving request.", ex);
                    return ServerResponse.status(HttpStatus.BAD_REQUEST)
                            .contentType(MediaType.APPLICATION_JSON)
                            .bodyValue(new ErrorDTO(ErrorMessage.FAIL_READ_REQUEST_CODE, ErrorMessage.FAIL_READ_REQUEST));
                }).onErrorResume(EmailTakenException.class, ex ->
                        ServerResponse.status(HttpStatus.CONFLICT)
                                .contentType(MediaType.APPLICATION_JSON)
                                .bodyValue(new ErrorDTO(ex.getCode(), ex.getMessage()))
                ).onErrorResume(UserException.class, ex ->
                        ServerResponse.badRequest()
                                .contentType(MediaType.APPLICATION_JSON)
                                .bodyValue(new ErrorDTO(ex.getCode(), ex.getMessage()))
                ).onErrorResume(Exception.class, ex -> {
                    logger.error("Unhandled error occurred.", ex);
                    return ServerResponse.status(HttpStatus.BAD_REQUEST)
                            .contentType(MediaType.APPLICATION_JSON)
                            .bodyValue(new ErrorDTO(ErrorMessage.UNKNOWN_CODE, "We are sorry, something went wrong. Please try again later."));
                });
    }
}
