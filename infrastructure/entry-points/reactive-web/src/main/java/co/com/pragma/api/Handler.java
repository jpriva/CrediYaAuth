package co.com.pragma.api;

import co.com.pragma.api.constants.ApiConstants;
import co.com.pragma.api.dto.*;
import co.com.pragma.api.mapper.FilterMapper;
import co.com.pragma.api.mapper.UserMapper;
import co.com.pragma.model.jwt.gateways.JwtProviderPort;
import co.com.pragma.model.user.filters.UserFilter;
import co.com.pragma.usecase.auth.AuthUseCase;
import co.com.pragma.usecase.user.UserUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.List;

import static co.com.pragma.model.constants.ErrorMessage.USER_NOT_FOUND;
import static co.com.pragma.model.constants.ErrorMessage.USER_NOT_FOUND_CODE;

@Component
@RequiredArgsConstructor
public class Handler {
    private final UserUseCase userUseCase;
    private final AuthUseCase authUseCase;
    private final JwtProviderPort jwtProvider;
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

    public Mono<ServerResponse> listenGETUserByIdNumberUseCase(ServerRequest serverRequest) {
        String idNumber = serverRequest.pathVariable(ApiConstants.ApiParams.ID_NUMBER_PARAM);
        return userUseCase.findByIdNumber(idNumber)
                .map(userMapper::toResponseDto)
                .flatMap(userDto ->
                        ServerResponse.ok()
                                .contentType(MediaType.APPLICATION_JSON)
                                .bodyValue(userDto)
                )
                .switchIfEmpty(Mono.defer(() -> {
                    ErrorDTO errorDto = ErrorDTO.builder()
                            .timestamp(Instant.now())
                            .path(serverRequest.path())
                            .code(USER_NOT_FOUND_CODE)
                            .message(USER_NOT_FOUND)
                            .build();
                    return ServerResponse.status(HttpStatus.NOT_FOUND)
                            .contentType(MediaType.APPLICATION_JSON)
                            .bodyValue(errorDto);
                }));
    }

    public Mono<ServerResponse> listenPOSTLoginUseCase(ServerRequest serverRequest) {
        return serverRequest.bodyToMono(LoginRequestDTO.class)
                .flatMap(loginRequest -> authUseCase.authenticate(loginRequest.getEmail(), loginRequest.getPassword()))
                .flatMap(authenticatedUser -> {
                    String token = jwtProvider.generateToken(authenticatedUser);
                    LoginResponseDTO loginResponse = LoginResponseDTO.builder().token(token).email(authenticatedUser.getEmail()).build();
                    return ServerResponse.ok()
                            .contentType(MediaType.APPLICATION_JSON)
                            .bodyValue(loginResponse);
                });
    }

    public Mono<ServerResponse> listenPOSTUsersByEmailsUseCase(ServerRequest serverRequest) {
        Flux<UserResponseDTO> usersFlux = serverRequest
                .bodyToMono(new ParameterizedTypeReference<List<String>>() {})
                .flatMapMany(userUseCase::findUsersByEmails)
                .map(userMapper::toResponseDto);

        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(usersFlux, UserResponseDTO.class);
    }

    public Mono<ServerResponse> listenPOSTFindUserEmailsByFilterUseCase(ServerRequest serverRequest) {
        Mono<List<String>> emailsListMono = serverRequest
                .bodyToMono(FindUsersRequestDTO.class)
                .map(userMapper::toUserFilter)
                .flatMapMany(userUseCase::findUserEmailsByFilter)
                .distinct()
                .collectList();

        return emailsListMono
                .flatMap(emails ->
                        ServerResponse.ok()
                                .contentType(MediaType.APPLICATION_JSON)
                                .bodyValue(emails)
                );
    }

    public Mono<ServerResponse> listenGETUserByEmailUseCase(ServerRequest serverRequest) {
        String email = serverRequest.pathVariable(ApiConstants.ApiParams.EMAIL_PARAM);
        return userUseCase.findByEmail(email)
                .map(userMapper::toResponseDto)
                .flatMap(userDto ->
                        ServerResponse.ok()
                                .contentType(MediaType.APPLICATION_JSON)
                                .bodyValue(userDto)
                )
                .switchIfEmpty(Mono.defer(() -> {
                    ErrorDTO errorDto = ErrorDTO.builder()
                            .timestamp(Instant.now())
                            .path(serverRequest.path())
                            .code(USER_NOT_FOUND_CODE)
                            .message(USER_NOT_FOUND)
                            .build();
                    return ServerResponse.status(HttpStatus.NOT_FOUND)
                            .contentType(MediaType.APPLICATION_JSON)
                            .bodyValue(errorDto);
                }));
    }

    public Mono<ServerResponse> listenGETUsersByFilterUseCase(ServerRequest serverRequest) {
        UserFilter filter = FilterMapper.toFilter(serverRequest.queryParams());
        Flux<UserResponseDTO> usersFlux = userUseCase.findUsersByFilter(filter)
                .distinct()
                .map(userMapper::toResponseDto);
        return usersFlux.collectList()
                .flatMap(userList -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(userList));
    }
}
