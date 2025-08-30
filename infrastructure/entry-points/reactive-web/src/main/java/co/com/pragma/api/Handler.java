package co.com.pragma.api;

import co.com.pragma.api.dto.UserRequestDTO;
import co.com.pragma.api.mapper.UserMapper;
import co.com.pragma.model.logs.gateways.LoggerPort;
import co.com.pragma.usecase.user.UserUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

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
                );
    }
}
