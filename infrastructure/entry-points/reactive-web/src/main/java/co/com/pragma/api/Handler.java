package co.com.pragma.api;

import co.com.pragma.api.dto.ErrorDTO;
import co.com.pragma.api.dto.UserRequestDTO;
import co.com.pragma.api.mapper.UserMapper;
import co.com.pragma.model.logs.gateways.LoggerPort;
import co.com.pragma.model.constants.ErrorMessage;
import co.com.pragma.model.exceptions.CustomException;
import co.com.pragma.usecase.user.UserUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.server.ServerWebInputException;
import reactor.core.publisher.Mono;

import java.time.Instant;

@Component
@RequiredArgsConstructor
public class Handler {
    private final UserUseCase userUseCase;
    private final LoggerPort logger;
    private final UserMapper userMapper;

    public Mono<ServerResponse> listenPOSTSaveUserUseCase(ServerRequest serverRequest) {
        return serverRequest.bodyToMono(UserRequestDTO.class)
                .map(userMapper::toDomain)
                .flatMap(userUseCase::saveUser)
                .map(userMapper::toResponseDto)
                .flatMap(savedUser ->
                        ServerResponse.status(HttpStatus.CREATED)
                                .contentType(MediaType.APPLICATION_JSON)
                                .bodyValue(savedUser)
                )
                .doOnError(ex -> logger.error(ex.getMessage(), ex))
                .onErrorResume(ServerWebInputException.class, ex ->
                        ServerResponse.status(HttpStatus.BAD_REQUEST)
                                .contentType(MediaType.APPLICATION_JSON)
                                .bodyValue(ErrorDTO.builder()
                                        .timestamp(Instant.now())
                                        .path(serverRequest.path())
                                        .code(ErrorMessage.FAIL_READ_REQUEST_CODE)
                                        .message(ErrorMessage.FAIL_READ_REQUEST)
                                        .build())
                ).onErrorResume(CustomException.class, ex ->
                        ServerResponse.status(HttpStatus.valueOf(ex.getWebStatus()))
                                .contentType(MediaType.APPLICATION_JSON)
                                .bodyValue(ErrorDTO.builder()
                                        .timestamp(ex.getTimestamp())
                                        .path(serverRequest.path())
                                        .code(ex.getCode())
                                        .message(ex.getMessage())
                                        .build())
                ).onErrorResume(Exception.class, ex ->
                     ServerResponse.status(HttpStatus.BAD_REQUEST)
                            .contentType(MediaType.APPLICATION_JSON)
                            .bodyValue(ErrorDTO.builder()
                                    .timestamp(Instant.now())
                                    .path(serverRequest.path())
                                    .code(ErrorMessage.UNKNOWN_CODE)
                                    .message(ErrorMessage.UNKNOWN_ERROR)
                                    .build())
                );
    }
}
