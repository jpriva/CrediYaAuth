package co.com.pragma.api;

import co.com.pragma.api.dto.ErrorDTO;
import co.com.pragma.api.dto.UserSaveRequestDTO;
import co.com.pragma.api.mapper.UserMapper;
import co.com.pragma.model.logs.gateways.LoggerPort;
import co.com.pragma.usecase.user.constants.ErrorMessage;
import co.com.pragma.usecase.user.exceptions.CustomException;
import co.com.pragma.usecase.user.UserUseCase;
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
    private final UserUseCase userUseCase;
    private final LoggerPort logger;

    public Mono<ServerResponse> listenPOSTSaveUserUseCase(ServerRequest serverRequest) {
        return serverRequest.bodyToMono(UserSaveRequestDTO.class)
                .map(UserMapper::toDomain)
                .flatMap(userUseCase::saveUser)
                .map(UserMapper::toResponseDto)
                .flatMap(savedUser ->
                        ServerResponse.status(HttpStatus.CREATED)
                                .contentType(MediaType.APPLICATION_JSON)
                                .bodyValue(savedUser)
                )
                .doOnError(ex -> logger.error(ex.getMessage(), ex))
                .onErrorResume(ServerWebInputException.class, ex ->
                        ServerResponse.status(HttpStatus.BAD_REQUEST)
                                .contentType(MediaType.APPLICATION_JSON)
                                .bodyValue(new ErrorDTO(ErrorMessage.FAIL_READ_REQUEST_CODE, ErrorMessage.FAIL_READ_REQUEST))
                ).onErrorResume(CustomException.class, ex ->
                        ServerResponse.status(HttpStatus.valueOf(ex.getWebStatus()))
                                .contentType(MediaType.APPLICATION_JSON)
                                .bodyValue(new ErrorDTO(ex.getCode(), ex.getMessage()))
                ).onErrorResume(Exception.class, ex ->
                     ServerResponse.status(HttpStatus.BAD_REQUEST)
                            .contentType(MediaType.APPLICATION_JSON)
                            .bodyValue(new ErrorDTO(
                                    ErrorMessage.UNKNOWN_CODE,
                                    ErrorMessage.UNKNOWN_ERROR
                            ))
                );
    }
}
