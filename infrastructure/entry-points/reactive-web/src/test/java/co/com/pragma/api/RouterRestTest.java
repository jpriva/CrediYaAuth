package co.com.pragma.api;

import co.com.pragma.api.dto.ErrorDTO;
import co.com.pragma.api.dto.UserResponseDTO;
import co.com.pragma.api.dto.UserSaveRequestDTO;
import co.com.pragma.model.logs.gateways.LoggerPort;
import co.com.pragma.model.user.Role;
import co.com.pragma.model.user.User;
import co.com.pragma.model.user.constants.DefaultValues;
import co.com.pragma.model.user.constants.ErrorMessage;
import co.com.pragma.model.user.exceptions.EmailTakenException;
import co.com.pragma.model.user.exceptions.UserFieldBlankException;
import co.com.pragma.usecase.user.UserUseCase;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ContextConfiguration(classes = {RouterRest.class, Handler.class})
@WebFluxTest
class RouterRestTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean
    private LoggerPort logger;

    @MockitoBean
    private UserUseCase userUseCase;

    private UserSaveRequestDTO requestDto;

    @BeforeEach
    void setup() {
        requestDto = UserSaveRequestDTO.builder()
                .name("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .idNumber("123456789")
                .idNumber("123456")
                .rolName(DefaultValues.DEFAULT_ROLE)
                .baseSalary(new BigDecimal(5000000))
                .build();
    }


    @Test
    void saveUser_shouldReturnCreated_whenValidRequest() {
        User useCaseResponse = User.builder()
                .userId(3)
                .name("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .idNumber("123456789")
                .idNumber("123456")
                .role(Role.builder().rolId(3).name(DefaultValues.DEFAULT_ROLE).description("Test Client").build())
                .baseSalary(new BigDecimal(5000000))
                .build();
        when(userUseCase.saveUser(any(User.class))).thenReturn(Mono.just(useCaseResponse));
        webTestClient.post()
                .uri(ApiConstants.ApiPaths.USERS_PATH)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(requestDto)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(UserResponseDTO.class)
                .value(response -> {
                    Assertions.assertThat(response).isInstanceOf(UserResponseDTO.class);
                    Assertions.assertThat(response.getUserId()).isEqualTo(3);
                    Assertions.assertThat(response.getRoleId()).isEqualTo(3);
                    Assertions.assertThat(response.getRoleName()).isEqualTo(DefaultValues.DEFAULT_ROLE);
                });
    }

    @Test
    void saveUser_shouldReturnConflict_whenEmailIsTaken() {
        when(userUseCase.saveUser(any(User.class))).thenReturn(Mono.error(new EmailTakenException()));
        webTestClient.post()
                .uri(ApiConstants.ApiPaths.USERS_PATH)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(requestDto)
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.CONFLICT);
    }

    @Test
    void saveUser_shouldReturnBadRequest_whenMissingRequiredFields() {
        when(userUseCase.saveUser(any(User.class))).thenReturn(Mono.error(new UserFieldBlankException("field")));
        webTestClient.post()
                .uri(ApiConstants.ApiPaths.USERS_PATH)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(requestDto)
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void saveUser_shouldReturnBadRequest_whenUnknownExceptionIsThrown() {
        when(userUseCase.saveUser(any(User.class))).thenReturn(Mono.error(new Exception()));
        webTestClient.post()
                .uri(ApiConstants.ApiPaths.USERS_PATH)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(requestDto)
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void saveUser_shouldReturnBadRequest_whenServerWebInputExceptionIsThrown() {
        webTestClient.post()
                .uri(ApiConstants.ApiPaths.USERS_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue("{FailedBody}")
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.BAD_REQUEST)
                .expectBody(ErrorDTO.class)
                .value(errorDTO ->
                        Assertions.assertThat(errorDTO.getCode()).isEqualTo(ErrorMessage.FAIL_READ_REQUEST_CODE)
                );
    }
}
