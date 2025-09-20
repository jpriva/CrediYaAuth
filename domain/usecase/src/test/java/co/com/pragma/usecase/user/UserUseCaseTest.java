package co.com.pragma.usecase.user;

import co.com.pragma.model.constants.LogMessages;
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
import static org.mockito.Mockito.*;

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

    @SuppressWarnings("unchecked")
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


        lenient().when(transactionalPort.transactional(any(Mono.class))).thenAnswer(invocation -> invocation.getArgument(0));
    }

    @Nested
    class SaveUserTests {

        @Test
        void saveUser_whenValid_shouldSucceed() {
            when(roleRepository.findOne(any(Role.class))).thenReturn(Mono.just(defaultRole));
            when(userRepository.exists(any(User.class))).thenReturn(Mono.just(false));
            when(passwordEncoderPort.encode(anyString())).thenReturn("hashed_password");
            when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
                User user = invocation.getArgument(0);
                return Mono.just(user.toBuilder().userId(1).build());
            });

            StepVerifier.create(userUseCase.saveUser(userToSave))
                    .expectNextMatches(savedUser ->
                            savedUser.getUserId() != null &&
                                    "hashed_password".equals(savedUser.getPassword()) &&
                                    savedUser.getRole().equals(defaultRole)
                    )
                    .verifyComplete();
        }

        @Test
        void saveUser_whenUserIsNull_shouldReturnError() {
            StepVerifier.create(userUseCase.saveUser(null))
                    .expectError(UserNullException.class)
                    .verify();
        }

        @Test
        void saveUser_whenRoleNotFound_shouldReturnError() {
            when(roleRepository.findOne(any(Role.class))).thenReturn(Mono.empty());

            StepVerifier.create(userUseCase.saveUser(userToSave))
                    .expectError(RoleNotFoundException.class)
                    .verify();
        }

        @Test
        void saveUser_whenEmailExists_shouldReturnError() {
            when(roleRepository.findOne(any(Role.class))).thenReturn(Mono.just(defaultRole));
            when(userRepository.exists(any(User.class)))
                    .thenReturn(Mono.just(true))
                    .thenReturn(Mono.just(false));

            StepVerifier.create(userUseCase.saveUser(userToSave))
                    .expectError(EmailTakenException.class)
                    .verify();
        }

        @Test
        void saveUser_whenIdNumberExists_shouldReturnError() {
            when(roleRepository.findOne(any(Role.class))).thenReturn(Mono.just(defaultRole));
            when(userRepository.exists(any(User.class)))
                    .thenReturn(Mono.just(false))
                    .thenReturn(Mono.just(true));

            StepVerifier.create(userUseCase.saveUser(userToSave))
                    .expectError(IdNumberTakenException.class)
                    .verify();
        }
    }

    @Nested
    class FindByIdNumberTests {

        @Test
        void findByIdNumber_whenFound_shouldReturnUser() {
            when(userRepository.findOne(any(User.class))).thenReturn(Mono.just(userToSave));
            when(roleRepository.findOne(any(Role.class))).thenReturn(Mono.just(defaultRole));

            StepVerifier.create(userUseCase.findByIdNumber("123456789"))
                    .expectNextMatches(foundUser -> "123456789".equals(foundUser.getIdNumber()))
                    .verifyComplete();
        }

        @Test
        void findByIdNumber_whenNotFound_shouldReturnEmpty() {
            when(userRepository.findOne(any(User.class))).thenReturn(Mono.empty());

            StepVerifier.create(userUseCase.findByIdNumber("123456789"))
                    .verifyComplete();
        }

        @Test
        void findByIdNumber_whenRepositoryFails_shouldLogError() {
            String idNumber = "123456789";
            RuntimeException dbError = new RuntimeException("DB is down");
            when(userRepository.findOne(any(User.class))).thenReturn(Mono.error(dbError));

            StepVerifier.create(userUseCase.findByIdNumber(idNumber))
                    .expectError(RuntimeException.class)
                    .verify();

            verify(logger).error(LogMessages.ERROR_FINDING_USER_BY_ID_NUMBER, idNumber, dbError);
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

        @Test
        void findUsersByEmails_whenRepositoryFails_shouldLogError() {
            // Arrange
            List<String> emails = List.of("test@example.com");
            RuntimeException dbError = new RuntimeException("DB is down");
            when(userRepository.findAllByEmail(anyList())).thenReturn(Flux.error(dbError));

            // Act & Assert
            StepVerifier.create(userUseCase.findUsersByEmails(emails))
                    .expectError(RuntimeException.class)
                    .verify();

            verify(logger).error(LogMessages.ERROR_FINDING_USERS_BY_EMAILS, emails, dbError);
        }
    }

    @Nested
    class FindUsersByFilterTests {
        @Test
        void findUsersByFilter_shouldReturnFluxOfUsers() {
            UserFilter filter = UserFilter.builder().name("John").build();
            when(userRepository.findUsersByFilter(filter)).thenReturn(Flux.just(userToSave));

            StepVerifier.create(userUseCase.findUsersByFilter(filter))
                    .expectNext(userToSave)
                    .verifyComplete();
        }

        @Test
        void findUsersByFilter_whenRepositoryFails_shouldLogError() {
            UserFilter filter = UserFilter.builder().name("John").build();
            RuntimeException dbError = new RuntimeException("DB is down");
            when(userRepository.findUsersByFilter(filter)).thenReturn(Flux.error(dbError));

            StepVerifier.create(userUseCase.findUsersByFilter(filter))
                    .expectError(RuntimeException.class)
                    .verify();

            verify(logger).error(LogMessages.ERROR_FINDING_USERS, dbError);
        }
    }

    @Nested
    class FindByEmailTests {
        @Test
        void findByEmail_shouldReturnUser() {
            String email = "john.doe@example.com";
            when(userRepository.findByEmail(email)).thenReturn(Mono.just(userToSave));

            StepVerifier.create(userUseCase.findByEmail(email))
                    .expectNext(userToSave)
                    .verifyComplete();
        }
    }

    @Nested
    class FindUserEmailsByFilterTests {
        @Test
        void findUserEmailsByFilter_shouldReturnFluxOfEmails() {
            UserFilter filter = UserFilter.builder().name("John").build();
            when(userRepository.findUserEmailsByFilter(filter)).thenReturn(Flux.just("john.doe@example.com"));

            StepVerifier.create(userUseCase.findUserEmailsByFilter(filter))
                    .expectNext("john.doe@example.com")
                    .verifyComplete();
        }

        @Test
        void findUserEmailsByFilter_whenRepositoryFails_shouldLogError() {
            UserFilter filter = UserFilter.builder().name("John").build();
            RuntimeException dbError = new RuntimeException("DB is down");
            when(userRepository.findUserEmailsByFilter(filter)).thenReturn(Flux.error(dbError));

            StepVerifier.create(userUseCase.findUserEmailsByFilter(filter))
                    .expectError(RuntimeException.class)
                    .verify();

            verify(logger).error(LogMessages.ERROR_FINDING_USER_EMAILS, dbError);
        }
    }
}