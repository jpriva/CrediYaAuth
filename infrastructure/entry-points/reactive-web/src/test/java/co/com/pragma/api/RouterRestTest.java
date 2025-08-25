package co.com.pragma.api;

import co.com.pragma.api.dto.ErrorDTO;
import co.com.pragma.api.dto.UserResponseDTO;
import co.com.pragma.api.dto.UserSaveRequestDTO;
import co.com.pragma.model.logs.gateways.LoggerPort;
import co.com.pragma.model.user.Role;
import co.com.pragma.model.user.User;
import co.com.pragma.model.user.exceptions.EmailTakenException;
import co.com.pragma.model.user.exceptions.SizeOutOfBoundsException;
import co.com.pragma.model.user.exceptions.UserFieldException;
import co.com.pragma.usecase.user.SaveUserUseCase;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.http.HttpStatusCode;
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
    private SaveUserUseCase saveUserUseCase;

    private UserSaveRequestDTO requestDto;

    @BeforeEach
    void setup(){
        requestDto = UserSaveRequestDTO.builder()
                .name("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .idNumber("123456789")
                .idNumber("123456")
                .rolName("CLIENTE")
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
                .role(Role.builder().rolId(3).name("CLIENTE").description("Test Client").build())
                .baseSalary(new BigDecimal(5000000))
                .build();
        when(saveUserUseCase.execute(any(User.class))).thenReturn(Mono.just(useCaseResponse));
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
                    Assertions.assertThat(response.getRoleName()).isEqualTo("CLIENTE");
                });
    }

    @Test
    void saveUser_shouldReturnConflict_whenEmailIsTaken() {
        when(saveUserUseCase.execute(any(User.class))).thenReturn(Mono.error(new EmailTakenException()));
        webTestClient.post()
                .uri(ApiConstants.ApiPaths.USERS_PATH)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(requestDto)
                .exchange()
                .expectStatus().isEqualTo(HttpStatusCode.valueOf(409));
    }

    @Test
    void saveUser_shouldReturnBadRequest_whenMissingRequiredFields() {
        when(saveUserUseCase.execute(any(User.class))).thenReturn(Mono.error(new UserFieldException()));
        webTestClient.post()
                .uri(ApiConstants.ApiPaths.USERS_PATH)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(requestDto)
                .exchange()
                .expectStatus().isEqualTo(HttpStatusCode.valueOf(400));
    }

    @Test
    void saveUser_shouldReturnBadRequest_whenUnknownExceptionIsThrown() {
        when(saveUserUseCase.execute(any(User.class))).thenReturn(Mono.error(new Exception()));
        webTestClient.post()
                .uri(ApiConstants.ApiPaths.USERS_PATH)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(requestDto)
                .exchange()
                .expectStatus().isEqualTo(HttpStatusCode.valueOf(400));
    }
}
