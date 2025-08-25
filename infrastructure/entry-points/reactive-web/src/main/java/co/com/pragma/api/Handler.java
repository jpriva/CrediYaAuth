package co.com.pragma.api;

import co.com.pragma.api.dto.UserDTO;
import co.com.pragma.api.mapper.UserMapper;
import co.com.pragma.model.user.User;
import co.com.pragma.usecase.user.IUserUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class Handler {
    private final IUserUseCase saveUser;

    public Mono<ServerResponse> listenPOSTSaveUserUseCase(ServerRequest serverRequest) {
        return serverRequest.bodyToMono(UserDTO.class)
                .map(UserMapper::toDomain)
                .flatMap(saveUser::save)
                .flatMap( savedUser ->
                        ServerResponse.ok()
                                .contentType(MediaType.APPLICATION_JSON)
                                .bodyValue(savedUser)
                );
    }
}
