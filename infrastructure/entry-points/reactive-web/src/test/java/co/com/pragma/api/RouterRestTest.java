package co.com.pragma.api;

import co.com.pragma.api.constants.ApiConstants;
import co.com.pragma.api.dto.ErrorDTO;
import co.com.pragma.api.dto.RoleDTO;
import co.com.pragma.api.dto.UserRequestDTO;
import co.com.pragma.api.dto.UserResponseDTO;
import co.com.pragma.api.mapper.UserMapper;
import co.com.pragma.model.constants.DefaultValues;
import co.com.pragma.model.constants.ErrorMessage;
import co.com.pragma.model.exceptions.EmailTakenException;
import co.com.pragma.model.exceptions.FieldBlankException;
import co.com.pragma.model.logs.gateways.LoggerPort;
import co.com.pragma.model.role.Role;
import co.com.pragma.model.user.User;
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

    @MockitoBean
    private UserMapper userMapper;

    private UserRequestDTO requestDto;
    private UserResponseDTO responseDto;

    @BeforeEach
    void setup() {
        requestDto = UserRequestDTO.builder()
                .name("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .idNumber("123456789")
                .rolId(3)
                .baseSalary(new BigDecimal(5000000))
                .build();

        User domainUser = User.builder().email("john.doe@example.com").build();
        when(userMapper.toDomain(any(UserRequestDTO.class))).thenReturn(domainUser);

        responseDto = UserResponseDTO.builder()
                .userId(3)
                .email("john.doe@example.com")
                .role(RoleDTO.builder().rolId(3).name(DefaultValues.DEFAULT_ROLE_NAME).build())
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
                .role(Role.builder().rolId(3).name(DefaultValues.DEFAULT_ROLE_NAME).description("Test Client").build())
                .baseSalary(new BigDecimal(5000000))
                .build();
        when(userUseCase.saveUser(any(User.class))).thenReturn(Mono.just(useCaseResponse));
        when(userMapper.toResponseDto(any(User.class))).thenReturn(responseDto);

        // Act & Assert
        webTestClient.post()
                .uri(ApiConstants.ApiPaths.USERS_PATH)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(requestDto)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(UserResponseDTO.class)
                .value(response -> {
                    Assertions.assertThat(response.getUserId()).isEqualTo(3);
                    Assertions.assertThat(response.getRole()).isNotNull();
                    Assertions.assertThat(response.getRole().getRolId()).isEqualTo(3);
                    Assertions.assertThat(response.getRole().getName()).isEqualTo(DefaultValues.DEFAULT_ROLE_NAME);
                });
    }

    @Test
    void saveUser_shouldReturnConflict_whenEmailIsTaken() {
        when(userUseCase.saveUser(any(User.class))).thenReturn(Mono.error(new EmailTakenException()));
        webTestClient.post()
                .uri(ApiConstants.ApiPaths.USERS_PATH)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestDto)
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.CONFLICT)
                .expectBody(ErrorDTO.class)
                .value(error -> Assertions.assertThat(error.getCode()).isEqualTo(ErrorMessage.EMAIL_TAKEN_CODE));
    }

    @Test
    void saveUser_shouldReturnBadRequest_whenMissingRequiredFields() {
        when(userUseCase.saveUser(any(User.class))).thenReturn(Mono.error(new FieldBlankException("field")));
        webTestClient.post()
                .uri(ApiConstants.ApiPaths.USERS_PATH)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestDto)
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.BAD_REQUEST)
                .expectBody(ErrorDTO.class)
                .value(error -> Assertions.assertThat(error.getCode()).isEqualTo(ErrorMessage.REQUIRED_FIELDS_CODE));
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
