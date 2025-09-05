package co.com.pragma.usecase.user;

import co.com.pragma.model.constants.DefaultValues;
import co.com.pragma.model.exceptions.*;
import co.com.pragma.model.logs.gateways.LoggerPort;
import co.com.pragma.model.password.gateways.PasswordEncoderPort;
import co.com.pragma.model.role.Role;
import co.com.pragma.model.role.gateways.RoleRepository;
import co.com.pragma.model.transaction.gateways.TransactionalPort;
import co.com.pragma.model.user.User;
import co.com.pragma.model.user.filters.UserFilter;
import co.com.pragma.model.user.gateways.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserUseCaseTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private RoleRepository roleRepository;
    @Mock
    private LoggerPort logger;
    @Mock
    private PasswordEncoderPort passwordEncoderPort;
    @Mock
    private TransactionalPort transactionalPort;

    @InjectMocks
    private UserUseCase userUseCase;

    private User userToSave;
    private Role defaultRole;

    @BeforeEach
    void setUp() {
        defaultRole = Role.builder().rolId(3).name(DefaultValues.DEFAULT_ROLE_NAME).build();
        userToSave = User.builder()
                .name("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .idNumber("123456789")
                .password("plain_password")
                .role(defaultRole)
                .baseSalary(new BigDecimal("50000"))
                .build();

        // Mock the transactional port to just return the mono it's given
        lenient().when(transactionalPort.transactional(any(Mono.class))).thenAnswer(invocation -> invocation.getArgument(0));
    }

    @Nested
    @DisplayName("saveUser Tests")
    class SaveUserTests {

        @Test
        @DisplayName("should save user successfully when all validations pass")
        void saveUser_whenValid_shouldSucceed() {
            // Arrange
            when(roleRepository.findOne(any(Role.class))).thenReturn(Mono.just(defaultRole));
            when(userRepository.exists(any(User.class))).thenReturn(Mono.just(false));
            when(passwordEncoderPort.encode(anyString())).thenReturn("hashed_password");
            when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
                User user = invocation.getArgument(0);
                return Mono.just(user.toBuilder().userId(1).build());
            });

            // Act & Assert
            StepVerifier.create(userUseCase.saveUser(userToSave))
                    .expectNextMatches(savedUser ->
                            savedUser.getUserId() != null &&
                                    "hashed_password".equals(savedUser.getPassword()) &&
                                    savedUser.getRole().equals(defaultRole)
                    )
                    .verifyComplete();
        }

        @Test
        @DisplayName("should return UserNullException when user is null")
        void saveUser_whenUserIsNull_shouldReturnError() {
            // Act & Assert
            StepVerifier.create(userUseCase.saveUser(null))
                    .expectError(UserNullException.class)
                    .verify();
        }

        @Test
        @DisplayName("should return RoleNotFoundException when role does not exist")
        void saveUser_whenRoleNotFound_shouldReturnError() {
            // Arrange
            when(roleRepository.findOne(any(Role.class))).thenReturn(Mono.empty());

            // Act & Assert
            StepVerifier.create(userUseCase.saveUser(userToSave))
                    .expectError(RoleNotFoundException.class)
                    .verify();
        }

        @Test
        @DisplayName("should return EmailTakenException when email already exists")
        void saveUser_whenEmailExists_shouldReturnError() {
            // Arrange
            when(roleRepository.findOne(any(Role.class))).thenReturn(Mono.just(defaultRole));
            // First exists call is for email, second for idNumber
            when(userRepository.exists(any(User.class)))
                    .thenReturn(Mono.just(true)) // Email exists
                    .thenReturn(Mono.just(false)); // ID number does not

            // Act & Assert
            StepVerifier.create(userUseCase.saveUser(userToSave))
                    .expectError(EmailTakenException.class)
                    .verify();
        }

        @Test
        @DisplayName("should return IdNumberTakenException when ID number already exists")
        void saveUser_whenIdNumberExists_shouldReturnError() {
            when(roleRepository.findOne(any(Role.class))).thenReturn(Mono.just(defaultRole));
            when(userRepository.exists(any(User.class)))
                    .thenReturn(Mono.just(false))
                    .thenReturn(Mono.just(true));

            // Act & Assert
            StepVerifier.create(userUseCase.saveUser(userToSave))
                    .expectError(IdNumberTakenException.class)
                    .verify();
        }
    }

    @Nested
    @DisplayName("findByIdNumber Tests")
    class FindByIdNumberTests {

        @Test
        @DisplayName("should return user when found")
        void findByIdNumber_whenFound_shouldReturnUser() {
            // Arrange
            when(userRepository.findOne(any(User.class))).thenReturn(Mono.just(userToSave));
            when(roleRepository.findOne(any(Role.class))).thenReturn(Mono.just(defaultRole));

            // Act & Assert
            StepVerifier.create(userUseCase.findByIdNumber("123456789"))
                    .expectNextMatches(foundUser -> "123456789".equals(foundUser.getIdNumber()))
                    .verifyComplete();
        }

        @Test
        @DisplayName("should return empty when not found")
        void findByIdNumber_whenNotFound_shouldReturnEmpty() {
            // Arrange
            when(userRepository.findOne(any(User.class))).thenReturn(Mono.empty());

            // Act & Assert
            StepVerifier.create(userUseCase.findByIdNumber("123456789"))
                    .verifyComplete();
        }
    }

    @Nested
    class FindUsersByEmailsTests {
        @Test
        void findUsersByEmails_shouldReturnFluxOfUsers() {
            String email1 = "john1.doe@example.com";
            String email2 = "john2.doe@example.com";
            User user1 = User.builder().name("john1").lastName("doe").userId(1).email(email1).build();
            User user2 = User.builder().name("john2").lastName("doe").userId(2).email(email2).build();
            List<String> emails = List.of(email1, email2);
            List<User> users = List.of(user1, user2);
            Flux<User> usersFlux = Flux.fromIterable(users);
            when(userRepository.findAllByEmail(anyList())).thenReturn(usersFlux);

            StepVerifier.create(userUseCase.findUsersByEmails(emails))
                    .expectNext(user1)
                    .expectNext(user2)
                    .verifyComplete();
        }
    }

    @Nested
    @DisplayName("findUsersByFilter Tests")
    class FindUsersByFilterTests {
        @Test
        @DisplayName("should return a flux of users matching the filter")
        void findUsersByFilter_shouldReturnFluxOfUsers() {
            // Arrange
            UserFilter filter = UserFilter.builder().name("John").build();
            when(userRepository.findUsersByFilter(filter)).thenReturn(Flux.just(userToSave));

            // Act & Assert
            StepVerifier.create(userUseCase.findUsersByFilter(filter))
                    .expectNext(userToSave)
                    .verifyComplete();
        }
    }

    @Nested
    @DisplayName("findByEmail Tests")
    class FindByEmailTests {
        @Test
        @DisplayName("should return a user for a given email")
        void findByEmail_shouldReturnUser() {
            // Arrange
            String email = "john.doe@example.com";
            when(userRepository.findByEmail(email)).thenReturn(Mono.just(userToSave));

            // Act & Assert
            StepVerifier.create(userUseCase.findByEmail(email))
                    .expectNext(userToSave)
                    .verifyComplete();
        }
    }

    @Nested
    @DisplayName("findUserEmailsByFilter Tests")
    class FindUserEmailsByFilterTests {
        @Test
        @DisplayName("should return a flux of emails matching the filter")
        void findUserEmailsByFilter_shouldReturnFluxOfEmails() {
            // Arrange
            UserFilter filter = UserFilter.builder().name("John").build();
            when(userRepository.findUserEmailsByFilter(filter)).thenReturn(Flux.just("john.doe@example.com"));

            // Act & Assert
            StepVerifier.create(userUseCase.findUserEmailsByFilter(filter))
                    .expectNext("john.doe@example.com")
                    .verifyComplete();
        }
    }
}