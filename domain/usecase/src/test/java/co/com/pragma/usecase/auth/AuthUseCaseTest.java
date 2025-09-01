package co.com.pragma.usecase.auth;

import co.com.pragma.model.constants.LogMessages;
import co.com.pragma.model.exceptions.FieldBlankException;
import co.com.pragma.model.exceptions.InvalidCredentialsException;
import co.com.pragma.model.logs.gateways.LoggerPort;
import co.com.pragma.model.password.gateways.PasswordEncoderPort;
import co.com.pragma.model.role.Role;
import co.com.pragma.model.role.gateways.RoleRepository;
import co.com.pragma.model.user.User;
import co.com.pragma.model.user.gateways.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthUseCaseTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private RoleRepository roleRepository;
    @Mock
    private PasswordEncoderPort passwordEncoderPort;
    @Mock
    private LoggerPort logger;

    @InjectMocks
    private AuthUseCase authUseCase;

    private User userFromRepo;
    private Role roleFromRepo;

    @BeforeEach
    void setUp() {
        userFromRepo = User.builder()
                .userId(1)
                .email("test@example.com")
                .role(Role.builder().rolId(1).build())
                .password("hashed_password")
                .build();
        roleFromRepo = Role.builder()
                .rolId(1)
                .name("ADMIN")
                .description("description")
                .build();
    }

    @Nested
    @DisplayName("Authentication Success")
    class SuccessScenario {
        @Test
        @DisplayName("should return authenticated user when credentials are valid")
        void authenticate_whenCredentialsAreValid_shouldReturnUser() {
            String email = "test@example.com";
            String rawPassword = "plain_password";

            when(userRepository.findWithPasswordByEmail(email)).thenReturn(Mono.just(userFromRepo));
            when(passwordEncoderPort.matches(rawPassword, "hashed_password")).thenReturn(true);
            when(roleRepository.findById(userFromRepo.getRole().getRolId())).thenReturn(Mono.just(roleFromRepo));

            StepVerifier.create(authUseCase.authenticate(email, rawPassword))
                    .assertNext(authenticatedUser -> {
                        assertThat(authenticatedUser.getUserId()).isEqualTo(userFromRepo.getUserId());
                        assertThat(authenticatedUser.getEmail()).isEqualTo(userFromRepo.getEmail());
                        assertThat(authenticatedUser.getPassword()).as("Password should be nullified after authentication").isNull();
                    })
                    .verifyComplete();
        }
    }

    @Nested
    @DisplayName("Authentication Failure")
    class FailureScenarios {

        @Test
        @DisplayName("should return InvalidCredentialsException when user is not found")
        void authenticate_whenUserNotFound_shouldReturnError() {
            String email = "notfound@example.com";
            String rawPassword = "any_password";

            when(userRepository.findWithPasswordByEmail(email)).thenReturn(Mono.empty());

            StepVerifier.create(authUseCase.authenticate(email, rawPassword))
                    .expectError(InvalidCredentialsException.class)
                    .verify();

            verify(logger).warn(LogMessages.USER_NOT_FOUND_FOR_AUTH, email);
        }

        @Test
        @DisplayName("should return InvalidCredentialsException when password does not match")
        void authenticate_whenPasswordMismatches_shouldReturnError() {
            String email = "test@example.com";
            String rawPassword = "wrong_password";

            when(userRepository.findWithPasswordByEmail(email)).thenReturn(Mono.just(userFromRepo));
            when(passwordEncoderPort.matches(rawPassword, "hashed_password")).thenReturn(false);

            StepVerifier.create(authUseCase.authenticate(email, rawPassword))
                    .expectError(InvalidCredentialsException.class)
                    .verify();
        }
    }

    @Nested
    @DisplayName("Input Validation")
    class InputValidation {

        @ParameterizedTest(name = "when email is \"{0}\"")
        @ValueSource(strings = {"", "   "})
        void authenticate_whenEmailIsBlank_shouldReturnError(String blankEmail) {
            StepVerifier.create(authUseCase.authenticate(blankEmail, "any_password"))
                    .expectError(FieldBlankException.class)
                    .verify();
        }

        @Test
        void authenticate_whenEmailIsNull_shouldReturnError() {
            StepVerifier.create(authUseCase.authenticate(null, "any_password"))
                    .expectError(FieldBlankException.class)
                    .verify();
        }

        @ParameterizedTest(name = "when password is \"{0}\"")
        @ValueSource(strings = {"", "   "})
        void authenticate_whenPasswordIsBlank_shouldReturnError(String blankPassword) {
            StepVerifier.create(authUseCase.authenticate("any@email.com", blankPassword))
                    .expectError(FieldBlankException.class)
                    .verify();
        }

        @Test
        void authenticate_whenPasswordIsNull_shouldReturnError() {
            StepVerifier.create(authUseCase.authenticate("any@email.com", null))
                    .expectError(FieldBlankException.class)
                    .verify();
        }
    }
}