package co.com.pragma.api;

import co.com.pragma.api.config.WebSecurityConfig;
import co.com.pragma.api.constants.ApiConstants;
import co.com.pragma.api.dto.*;
import co.com.pragma.api.exception.handler.CustomAccessDeniedHandler;
import co.com.pragma.api.exception.handler.GlobalExceptionHandler;
import co.com.pragma.api.mapper.FilterMapper;
import co.com.pragma.api.mapper.UserMapper;
import co.com.pragma.model.constants.DefaultValues;
import co.com.pragma.model.constants.ErrorMessage;
import co.com.pragma.model.exceptions.EmailTakenException;
import co.com.pragma.model.exceptions.FieldBlankException;
import co.com.pragma.model.exceptions.InvalidCredentialsException;
import co.com.pragma.model.jwt.gateways.JwtProviderPort;
import co.com.pragma.model.logs.gateways.LoggerPort;
import co.com.pragma.model.role.Role;
import co.com.pragma.model.user.User;
import co.com.pragma.model.user.filters.UserFilter;
import co.com.pragma.usecase.auth.AuthUseCase;
import co.com.pragma.usecase.user.UserUseCase;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.util.MultiValueMap;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ContextConfiguration(classes = {
        RouterRest.class, Handler.class,
        GlobalExceptionHandler.class, WebSecurityConfig.class,
        CustomAccessDeniedHandler.class
})
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

    @MockitoBean
    private FilterMapper filterMapper;

    @MockitoBean
    private AuthUseCase authUseCase;

    @MockitoBean
    private JwtProviderPort jwtProvider;


    private UserRequestDTO requestDto;
    private UserResponseDTO responseDto;
    private User domainUser;

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

        domainUser = User.builder()
                .name(requestDto.getName())
                .lastName(requestDto.getLastName())
                .email(requestDto.getEmail())
                .idNumber(requestDto.getIdNumber())
                .role(Role.builder().rolId(requestDto.getRolId()).build())
                .baseSalary(requestDto.getBaseSalary())
                .build();

        when(userMapper.toDomain(any(UserRequestDTO.class))).thenReturn(domainUser);

        responseDto = UserResponseDTO.builder()
                .userId(3)
                .email("john.doe@example.com")
                .idNumber("123456789")
                .role(RoleDTO.builder().rolId(3).name(DefaultValues.DEFAULT_ROLE_NAME).build())
                .baseSalary(new BigDecimal(5000000))
                .build();
    }

    @Test
    @WithMockUser(authorities = "ADMIN")
    void saveUser_shouldReturnCreated_whenValidRequest() {
        when(userMapper.toDomain(any(UserRequestDTO.class))).thenReturn(domainUser);

        User useCaseResponse = User.builder()
                .userId(3)
                .name("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .idNumber("123456789")
                .password("password")
                .role(Role.builder().rolId(3).name(DefaultValues.DEFAULT_ROLE_NAME).description("Test Client").build())
                .baseSalary(new BigDecimal(5000000))
                .build();
        when(userUseCase.saveUser(any(User.class))).thenReturn(Mono.just(useCaseResponse));
        when(userMapper.toResponseDto(any(User.class))).thenReturn(responseDto);

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
    @WithMockUser(authorities = "ADMIN")
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
    @WithMockUser(authorities = "ADMIN")
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
    @WithMockUser(authorities = "ADMIN")
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

    @Test
    @WithMockUser(authorities = "ADMIN")
    void findUserByIdNumber_shouldReturnOk_whenUserIsFound() {
        String idNumber = "123456789";
        User useCaseResponse = User.builder()
                .userId(3)
                .name("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .idNumber(idNumber)
                .role(Role.builder().rolId(3).name(DefaultValues.DEFAULT_ROLE_NAME).description("Test Client").build())
                .baseSalary(new BigDecimal(5000000))
                .build();
        when(userUseCase.findByIdNumber(idNumber)).thenReturn(Mono.just(useCaseResponse));
        when(userMapper.toResponseDto(any(User.class))).thenReturn(responseDto);

        webTestClient.get()
                .uri(ApiConstants.ApiPaths.USER_BY_ID_NUMBER_PATH, idNumber)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(UserResponseDTO.class)
                .value(userResponse ->
                        Assertions.assertThat(userResponse.getEmail()).isEqualTo("john.doe@example.com")
                );
    }

    @Test
    @WithMockUser(authorities = "ADMIN")
    void findUserByIdNumber_shouldReturnNotFound_whenUserDoesNotExist() {
        String idNumber = "999999999";
        when(userUseCase.findByIdNumber(idNumber)).thenReturn(Mono.empty());

        webTestClient.get()
                .uri(ApiConstants.ApiPaths.USER_BY_ID_NUMBER_PATH, idNumber)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody(ErrorDTO.class)
                .value(error -> {
                    Assertions.assertThat(error.getCode()).isEqualTo(ErrorMessage.USER_NOT_FOUND_CODE);
                    Assertions.assertThat(error.getMessage()).isEqualTo(ErrorMessage.USER_NOT_FOUND);
                });
    }

    @Test
    void login_shouldReturnOk_whenCredentialsAreValid() {
        LoginRequestDTO loginRequest = new LoginRequestDTO("test@example.com", "password");
        User authenticatedUser = User.builder().email("test@example.com").build();
        String jwtToken = "fake.jwt.token";

        when(authUseCase.authenticate(loginRequest.getEmail(), loginRequest.getPassword())).thenReturn(Mono.just(authenticatedUser));
        when(jwtProvider.generateToken(authenticatedUser)).thenReturn(jwtToken);

        webTestClient.post()
                .uri(ApiConstants.ApiPaths.LOGIN_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(loginRequest)
                .exchange()
                .expectStatus().isOk()
                .expectBody(LoginResponseDTO.class)
                .value(response -> {
                    Assertions.assertThat(response.getToken()).isEqualTo(jwtToken);
                    Assertions.assertThat(response.getEmail()).isEqualTo(loginRequest.getEmail());
                });
    }

    @Test
    void login_shouldReturnUnauthorized_whenCredentialsAreInvalid() {
        LoginRequestDTO loginRequest = new LoginRequestDTO("test@example.com", "wrong-password");
        when(authUseCase.authenticate(any(String.class), any(String.class))).thenReturn(Mono.error(new InvalidCredentialsException()));

        webTestClient.post()
                .uri(ApiConstants.ApiPaths.LOGIN_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(loginRequest)
                .exchange()
                .expectStatus().isUnauthorized();
    }

    @Test
    @WithMockUser(authorities = "ASESOR")
    void findUsersByEmails_shouldReturnOkWithUserList() {
        List<String> emailList = List.of("john.doe@example.com");
        when(userUseCase.findUsersByEmails(emailList)).thenReturn(Flux.just(User.builder().email("john.doe@example.com").build()));
        when(userMapper.toResponseDto(any(User.class))).thenReturn(responseDto);

        webTestClient.post()
                .uri(ApiConstants.ApiPaths.USERS_BY_EMAIL_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(emailList)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(UserResponseDTO.class)
                .hasSize(1)
                .value(users -> Assertions.assertThat(users.get(0).getEmail()).isEqualTo("john.doe@example.com"));
    }

    @Test
    @WithMockUser(authorities = "ASESOR")
    void findUserEmailsByFilter_shouldReturnOkWithEmailList() {
        FindUsersRequestDTO filterRequest = new FindUsersRequestDTO();
        UserFilter userFilter = UserFilter.builder().build();
        List<String> emailList = List.of("test@example.com");
        when(userMapper.toUserFilter(any(FindUsersRequestDTO.class))).thenReturn(userFilter);
        when(userUseCase.findUserEmailsByFilter(any(UserFilter.class))).thenReturn(Flux.fromIterable(emailList));

        webTestClient.post()
                .uri(ApiConstants.ApiPaths.USER_EMAILS_BY_FILTER_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(filterRequest)
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    @WithMockUser(authorities = "ASESOR")
    void findUserByEmail_shouldReturnOk_whenUserIsFound() {
        String email = "john.doe@example.com";
        when(userUseCase.findByEmail(email)).thenReturn(Mono.just(User.builder().email(email).build()));
        when(userMapper.toResponseDto(any(User.class))).thenReturn(responseDto);

        webTestClient.get()
                .uri(ApiConstants.ApiPaths.USER_BY_EMAIL_PATH, email)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(UserResponseDTO.class)
                .value(userResponse -> Assertions.assertThat(userResponse.getEmail()).isEqualTo(email));
    }

    @Test
    @WithMockUser(authorities = "ASESOR")
    void findUsersByFilter_shouldReturnOkWithUserList() {
        MultiValueMap<String, String> mockMultiValueMap = mock(MultiValueMap.class);
        when(filterMapper.toFilter(mockMultiValueMap)).thenReturn(UserFilter.builder().build());
        when(userUseCase.findUsersByFilter(any(UserFilter.class))).thenReturn(Flux.just(User.builder().build()));
        when(userMapper.toResponseDto(any(User.class))).thenReturn(responseDto);

        webTestClient.get()
                .uri(uriBuilder -> uriBuilder.path(ApiConstants.ApiPaths.USERS_BY_FILTER_PATH).queryParam("name", "John").build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(UserResponseDTO.class)
                .hasSize(1);
    }
}
