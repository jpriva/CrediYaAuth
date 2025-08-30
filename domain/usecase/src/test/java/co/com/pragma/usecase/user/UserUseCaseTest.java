package co.com.pragma.usecase.user;

import co.com.pragma.model.exceptions.*;
import co.com.pragma.model.logs.gateways.LoggerPort;
import co.com.pragma.model.transaction.gateways.TransactionalPort;
import co.com.pragma.model.role.Role;
import co.com.pragma.model.user.User;
import co.com.pragma.model.constants.DefaultValues;
import co.com.pragma.model.role.gateways.RoleRepository;
import co.com.pragma.model.user.gateways.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.util.stream.Stream;
import java.time.LocalDate;

import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
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
    private TransactionalPort transactionalPort;

    @InjectMocks
    private UserUseCase userUseCase;

    private User.UserBuilder validUser;
    private Role clientRole;

    @BeforeEach
    void setUp() {
        clientRole = Role.builder()
                .rolId(DefaultValues.DEFAULT_ROLE_ID)
                .name(DefaultValues.DEFAULT_ROLE_NAME)
                .description(DefaultValues.DEFAULT_ROLE_DESCRIPTION)
                .build();
        validUser = User.builder()
                .name("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .baseSalary(new BigDecimal("5000000"))
                .idNumber("123456789")
                .phone("+57123456789")
                .address("123 Main St")
                .birthDate(LocalDate.now());

        lenient().when(transactionalPort.transactional(any(Mono.class))).then(returnsFirstArg());
    }

    @Test
    @DisplayName("should save a user when all data is valid and a role is provided")
    void save_shouldSaveUser_whenDataIsValidAndRolIsProvided() {

        User userToSave = validUser.role(clientRole).build();
        User savedUser = userToSave.toBuilder().userId(1).build();

        when(roleRepository.findOne(clientRole)).thenReturn(Mono.just(clientRole));
        when(userRepository.exists(argThat(u -> u != null && u.getEmail() != null && u.getEmail().equals(userToSave.getEmail()))))
                .thenReturn(Mono.just(false));
        when(userRepository.exists(argThat(u -> u != null && u.getIdNumber() != null && u.getIdNumber().equals(userToSave.getIdNumber()))))
                .thenReturn(Mono.just(false));
        when(userRepository.save(any(User.class))).thenReturn(Mono.just(savedUser));

        StepVerifier.create(userUseCase.saveUser(userToSave))
                .expectNext(savedUser)
                .verifyComplete();
    }

    @Test
    @DisplayName("should save a user with default role when role is not provided")
    void save_shouldSaveUserWithDefaultRol_whenRolIsNotProvided() {

        User userWithoutRol = validUser.role(null).build();
        User savedUser = userWithoutRol.toBuilder().userId(1).role(clientRole).build();

        when(roleRepository.findOne(any(Role.class))).thenReturn(Mono.just(clientRole));
        when(userRepository.exists(argThat(u -> u != null && u.getEmail() != null && u.getEmail().equals(userWithoutRol.getEmail()))))
                .thenReturn(Mono.just(false));
        when(userRepository.exists(argThat(u -> u != null && u.getIdNumber() != null && u.getIdNumber().equals(userWithoutRol.getIdNumber()))))
                .thenReturn(Mono.just(false));
        when(userRepository.save(any(User.class))).thenReturn(Mono.just(savedUser));

        StepVerifier.create(userUseCase.saveUser(userWithoutRol))
                .expectNext(savedUser)
                .verifyComplete();
    }

    @Test
    @DisplayName("should save a user with default role when role name is null")
    void save_shouldSaveUserWithDefaultRol_whenRolNameIsNull() {

        User userWithEmptyRol = validUser.role(Role.builder().name(null).build()).build();
        User userWithClientRol = userWithEmptyRol.toBuilder().role(clientRole).build();
        User savedUser = userWithClientRol.toBuilder().userId(1).build();

        when(roleRepository.findOne(any(Role.class))).thenReturn(Mono.just(clientRole));
        when(userRepository.exists(argThat(u -> u != null && u.getEmail() != null && u.getEmail().equals(userWithEmptyRol.getEmail()))))
                .thenReturn(Mono.just(false));
        when(userRepository.exists(argThat(u -> u != null && u.getIdNumber() != null && u.getIdNumber().equals(userWithEmptyRol.getIdNumber()))))
                .thenReturn(Mono.just(false));
        when(userRepository.save(any(User.class))).thenReturn(Mono.just(savedUser));


        StepVerifier.create(userUseCase.saveUser(userWithEmptyRol))
                .expectNext(savedUser)
                .verifyComplete();
    }

    @Nested
    @DisplayName("Validation Failure Tests")
    class ValidationFailureTests {

        private static Stream<String> invalidStrings() {
            return Stream.of(null, "", "   ");
        }

        @Test
        @DisplayName("saveUser should return UserNullException when user is null")
        void save_shouldReturnUserFieldException_whenUserIsNull() {
            StepVerifier.create(userUseCase.saveUser(null))
                    .expectError(UserNullException.class)
                    .verify();
        }

        @ParameterizedTest(name = "when name is \"{0}\"")
        @MethodSource("invalidStrings")
        @DisplayName("saveUser should return FieldBlankException for invalid name")
        void save_shouldReturnUserFieldBlankException_whenNameFieldIsInvalid(String invalidName) {
            User invalidUser = validUser.name(invalidName).build();
            StepVerifier.create(userUseCase.saveUser(invalidUser))
                    .expectError(FieldBlankException.class)
                    .verify();
        }

        @ParameterizedTest(name = "when last name is \"{0}\"")
        @MethodSource("invalidStrings")
        @DisplayName("saveUser should return FieldBlankException for invalid last name")
        void save_shouldReturnUserFieldBlankException_whenLastNameFieldIsInvalid(String invalidLastName) {
            User invalidUser = validUser.lastName(invalidLastName).build();
            StepVerifier.create(userUseCase.saveUser(invalidUser))
                    .expectError(FieldBlankException.class)
                    .verify();
        }

        @ParameterizedTest(name = "when email is \"{0}\"")
        @MethodSource("invalidStrings")
        @DisplayName("saveUser should return FieldBlankException for invalid email")
        void save_shouldReturnUserFieldBlankException_whenEmailFieldIsInvalid(String invalidEmail) {
            User invalidUser = validUser.email(invalidEmail).build();
            StepVerifier.create(userUseCase.saveUser(invalidUser))
                    .expectError(FieldBlankException.class)
                    .verify();
        }

        @ParameterizedTest(name = "when idNumber is \"{0}\"")
        @MethodSource("invalidStrings")
        @DisplayName("saveUser should return FieldBlankException for invalid idNumber")
        void save_shouldReturnUserFieldBlankException_whenIdNumberFieldIsInvalid(String invalidIdNumber) {
            User invalidUser = validUser.idNumber(invalidIdNumber).build();
            StepVerifier.create(userUseCase.saveUser(invalidUser))
                    .expectError(FieldBlankException.class)
                    .verify();
        }

        @Test
        @DisplayName("saveUser should return FieldBlankException when salary is null")
        void save_shouldReturnUserFieldBlankException_whenSalaryFieldIsNull() {
            User invalidUser = validUser.baseSalary(null).build();
            StepVerifier.create(userUseCase.saveUser(invalidUser))
                    .expectError(FieldBlankException.class)
                    .verify();
        }

        @ParameterizedTest(name = "when salary is {0}")
        @ValueSource(strings = {"-1", "15000001"}) // 2. Se usan valores límite para probar el rango.
        @DisplayName("saveUser should return SalaryUnboundException for out-of-range salaries")
        void save_shouldReturnSalaryUnboundException_whenSalaryIsOutOfRange(String salary) {
            User invalidUser = validUser.baseSalary(new BigDecimal(salary)).build();
            StepVerifier.create(userUseCase.saveUser(invalidUser))
                    .expectError(SalaryUnboundException.class)
                    .verify();
        }

        @Test
        @DisplayName("saveUser should return EmailFormatException for an invalid email format")
        void save_shouldReturnEmailFormatException_whenEmailIsInvalid() {
            User invalidUser = validUser.email("not-a-valid-email").build();
            StepVerifier.create(userUseCase.saveUser(invalidUser))
                    .expectError(EmailFormatException.class)
                    .verify();
        }

        @Test
        @DisplayName("saveUser should return FieldSizeOutOfBoundsException when name is too long")
        void save_shouldReturnSizeOutOfBoundsException_whenNameIsTooLong() {
            String longString = "a".repeat(DefaultValues.MAX_LENGTH_NAME+1);
            User invalidUser = validUser.name(longString).build();
            StepVerifier.create(userUseCase.saveUser(invalidUser))
                    .expectError(FieldSizeOutOfBoundsException.class)
                    .verify();
        }

        @Test
        @DisplayName("saveUser should return FieldSizeOutOfBoundsException when last name is too long")
        void save_shouldReturnSizeOutOfBoundsException_whenLastNameIsTooLong() {
            String longString = "a".repeat(DefaultValues.MAX_LENGTH_LAST_NAME+1);
            User invalidUser = validUser.lastName(longString).build();
            StepVerifier.create(userUseCase.saveUser(invalidUser))
                    .expectError(FieldSizeOutOfBoundsException.class)
                    .verify();
        }

        @Test
        @DisplayName("saveUser should return FieldSizeOutOfBoundsException when email is too long")
        void save_shouldReturnSizeOutOfBoundsException_whenEmailIsTooLong() {
            String longString = "a".repeat(DefaultValues.MAX_LENGTH_EMAIL+1);
            User invalidUser = validUser.email(longString).build();
            StepVerifier.create(userUseCase.saveUser(invalidUser))
                    .expectError(FieldSizeOutOfBoundsException.class)
                    .verify();
        }

        @Test
        @DisplayName("saveUser should return FieldSizeOutOfBoundsException when ID number is too long")
        void save_shouldReturnSizeOutOfBoundsException_whenIdNumberIsTooLong() {
            String longString = "a".repeat(DefaultValues.MAX_LENGTH_ID_NUMBER+1);
            User invalidUser = validUser.idNumber(longString).build();
            StepVerifier.create(userUseCase.saveUser(invalidUser))
                    .expectError(FieldSizeOutOfBoundsException.class)
                    .verify();
        }

        @Test
        @DisplayName("saveUser should return FieldSizeOutOfBoundsException when phone is too long")
        void save_shouldReturnSizeOutOfBoundsException_whenPhoneIsTooLong() {
            String longString = "a".repeat(DefaultValues.MAX_LENGTH_PHONE+1);
            User invalidUser = validUser.phone(longString).build();
            StepVerifier.create(userUseCase.saveUser(invalidUser))
                    .expectError(FieldSizeOutOfBoundsException.class)
                    .verify();
        }

        @Test
        @DisplayName("saveUser should return FieldSizeOutOfBoundsException when address is too long")
        void save_shouldReturnSizeOutOfBoundsException_whenAddressIsTooLong() {
            String longString = "a".repeat(DefaultValues.MAX_LENGTH_ADDRESS+1);
            User invalidUser = validUser.address(longString).build();
            StepVerifier.create(userUseCase.saveUser(invalidUser))
                    .expectError(FieldSizeOutOfBoundsException.class)
                    .verify();
        }
    }

    @Nested
    @DisplayName("Repository Interaction Failure Tests")
    class RepositoryFailureTests {

        @Test
        @DisplayName("should return RoleNotFoundException when the role does not exist")
        void save_shouldReturnRoleNotFoundException_whenRoleDoesNotExist() {
            User userToSave = validUser.role(clientRole).build();
            when(roleRepository.findOne(clientRole)).thenReturn(Mono.empty());

            StepVerifier.create(userUseCase.saveUser(userToSave))
                    .expectError(RoleNotFoundException.class)
                    .verify();
        }

        @Test
        @DisplayName("should return EmailTakenException when the email already exists")
        void save_shouldReturnEmailTakenException_whenEmailAlreadyExists() {
            User userToSave = validUser.role(clientRole).build();

            when(roleRepository.findOne(clientRole)).thenReturn(Mono.just(clientRole));
            when(userRepository.exists(argThat(u -> u != null && u.getEmail() != null && u.getEmail().equals(userToSave.getEmail()))))
                    .thenReturn(Mono.just(true));
            when(userRepository.exists(argThat(u -> u != null && u.getIdNumber() != null && u.getIdNumber().equals(userToSave.getIdNumber()))))
                    .thenReturn(Mono.just(false));

            StepVerifier.create(userUseCase.saveUser(userToSave))
                    .expectError(EmailTakenException.class)
                    .verify();
        }

        @Test
        @DisplayName("should return IdNumberTakenException when the ID number already exists")
        void save_shouldReturnIdNumberTakenException_whenIdNumberAlreadyExists() {
            User userToSave = validUser.role(clientRole).build();
            User savedUser = userToSave.toBuilder().userId(1).build();

            when(roleRepository.findOne(clientRole)).thenReturn(Mono.just(clientRole));
            when(userRepository.exists(argThat(u -> u != null && u.getEmail() != null && u.getEmail().equals(userToSave.getEmail()))))
                    .thenReturn(Mono.just(false));
            if (DefaultValues.ID_NUMBER_UNIQUE) {
                when(userRepository.exists(argThat(u -> u != null && u.getIdNumber() != null && u.getIdNumber().equals(userToSave.getIdNumber()))))
                        .thenReturn(Mono.just(true));
                StepVerifier.create(userUseCase.saveUser(userToSave))
                        .expectError(IdNumberTakenException.class)
                        .verify();
            } else {
                when(userRepository.save(any(User.class))).thenReturn(Mono.just(savedUser));
                StepVerifier.create(userUseCase.saveUser(userToSave))
                    .expectNext(savedUser)
                    .verifyComplete();
            }
        }
    }
}