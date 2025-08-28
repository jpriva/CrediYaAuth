package co.com.pragma.usecase.user;

import co.com.pragma.model.exceptions.*;
import co.com.pragma.model.logs.gateways.LoggerPort;
import co.com.pragma.model.transaction.gateways.TransactionalPort;
import co.com.pragma.model.user.Role;
import co.com.pragma.model.user.User;
import co.com.pragma.model.constants.DefaultValues;
import co.com.pragma.model.user.gateways.RoleRepository;
import co.com.pragma.model.user.gateways.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
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
    private TransactionalPort operator;

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

        lenient().when(operator.transactional(any(Mono.class))).then(returnsFirstArg());
    }

    @Test
    void save_shouldSaveUser_whenDataIsValidAndRolIsProvided() {

        User userToSave = validUser.role(clientRole).build();
        User savedUser = userToSave.toBuilder().userId(1).build();

        when(roleRepository.findOne(clientRole)).thenReturn(Mono.just(clientRole));
        when(userRepository.exists(any(User.class))).thenReturn(Mono.just(false));
        when(userRepository.save(any(User.class))).thenReturn(Mono.just(savedUser));

        StepVerifier.create(userUseCase.saveUser(userToSave))
                .expectNext(savedUser)
                .verifyComplete();
    }

    @Test
    void save_shouldSaveUserWithDefaultRol_whenRolIsNotProvided() {

        User userWithoutRol = validUser.role(null).build();

        User savedUser = userWithoutRol.toBuilder().userId(1).role(clientRole).build();

        when(roleRepository.findOne(any(Role.class))).thenReturn(Mono.just(clientRole));
        when(userRepository.exists(any(User.class))).thenReturn(Mono.just(false));
        when(userRepository.save(any(User.class))).thenReturn(Mono.just(savedUser));

        StepVerifier.create(userUseCase.saveUser(userWithoutRol))
                .expectNext(savedUser)
                .verifyComplete();
    }

    @Test
    void save_shouldSaveUserWithDefaultRol_whenRolNameIsNull() {

        User userWithEmptyRol = validUser.role(Role.builder().name(null).build()).build();

        User userWithClientRol = userWithEmptyRol.toBuilder().role(clientRole).build();
        User savedUser = userWithClientRol.toBuilder().userId(1).build();

        when(roleRepository.findOne(any(Role.class))).thenReturn(Mono.just(clientRole));
        when(userRepository.exists(any(User.class))).thenReturn(Mono.just(false));
        when(userRepository.save(any(User.class))).thenReturn(Mono.just(savedUser));


        StepVerifier.create(userUseCase.saveUser(userWithEmptyRol))
                .expectNext(savedUser)
                .verifyComplete();
    }

    @Nested
    @DisplayName("Validation Failure Tests")
    class ValidationFailureTests {

        @Test
        void save_shouldReturnUserFieldException_whenUserIsNull() {
            StepVerifier.create(userUseCase.saveUser(null))
                    .expectError(UserNullException.class)
                    .verify();
        }

        @Test
        void save_shouldReturnUserFieldBlankException_whenNameFieldIsBlankOrNull() {
            User invalidUser = validUser.name("  ").build();
            StepVerifier.create(userUseCase.saveUser(invalidUser))
                    .expectError(FieldBlankException.class)
                    .verify();
            User invalidUser2 = validUser.name(null).build();
            StepVerifier.create(userUseCase.saveUser(invalidUser2))
                    .expectError(FieldBlankException.class)
                    .verify();
        }

        @Test
        void save_shouldReturnUserFieldBlankException_whenLastNameFieldIsBlankOrNull() {
            User invalidUser = validUser.lastName("  ").build();
            StepVerifier.create(userUseCase.saveUser(invalidUser))
                    .expectError(FieldBlankException.class)
                    .verify();
            User invalidUser2 = validUser.lastName(null).build();
            StepVerifier.create(userUseCase.saveUser(invalidUser2))
                    .expectError(FieldBlankException.class)
                    .verify();
        }

        @Test
        void save_shouldReturnUserFieldBlankException_whenEmailFieldIsBlankOrNull() {
            User invalidUser = validUser.email("  ").build();
            StepVerifier.create(userUseCase.saveUser(invalidUser))
                    .expectError(FieldBlankException.class)
                    .verify();
            User invalidUser2 = validUser.email(null).build();
            StepVerifier.create(userUseCase.saveUser(invalidUser2))
                    .expectError(FieldBlankException.class)
                    .verify();
        }

        @Test
        void save_shouldReturnUserFieldBlankException_whenSalaryFieldIsNull() {
            User invalidUser = validUser.baseSalary(null).build();
            StepVerifier.create(userUseCase.saveUser(invalidUser))
                    .expectError(FieldBlankException.class)
                    .verify();
        }

        @Test
        void save_shouldReturnSalaryUnboundException_whenSalaryIsNegative() {
            User invalidUser = validUser.baseSalary(DefaultValues.MIN_SALARY.subtract(BigDecimal.ONE)).build();
            StepVerifier.create(userUseCase.saveUser(invalidUser))
                    .expectError(SalaryUnboundException.class)
                    .verify();
        }

        @Test
        void save_shouldReturnSalaryUnboundException_whenSalaryIsTooHigh() {
            User invalidUser = validUser.baseSalary(DefaultValues.MAX_SALARY.add(BigDecimal.ONE)).build();
            StepVerifier.create(userUseCase.saveUser(invalidUser))
                    .expectError(SalaryUnboundException.class)
                    .verify();
        }

        @Test
        void save_shouldReturnEmailFormatException_whenEmailIsInvalid() {
            User invalidUser = validUser.email("a".repeat(10)).build();
            StepVerifier.create(userUseCase.saveUser(invalidUser))
                    .expectError(EmailFormatException.class)
                    .verify();
        }

        @Test
        void save_shouldReturnSizeOutOfBoundsException_whenNameIsTooLong() {
            String longString = "a".repeat(DefaultValues.MAX_LENGTH_NAME+1);
            User invalidUser = validUser.name(longString).build();
            StepVerifier.create(userUseCase.saveUser(invalidUser))
                    .expectError(FieldSizeOutOfBoundsException.class)
                    .verify();
        }

        @Test
        void save_shouldReturnSizeOutOfBoundsException_whenLastNameIsTooLong() {
            String longString = "a".repeat(DefaultValues.MAX_LENGTH_LAST_NAME+1);
            User invalidUser = validUser.lastName(longString).build();
            StepVerifier.create(userUseCase.saveUser(invalidUser))
                    .expectError(FieldSizeOutOfBoundsException.class)
                    .verify();
        }

        @Test
        void save_shouldReturnSizeOutOfBoundsException_whenEmailIsTooLong() {
            String longString = "a".repeat(DefaultValues.MAX_LENGTH_EMAIL+1);
            User invalidUser = validUser.email(longString).build();
            StepVerifier.create(userUseCase.saveUser(invalidUser))
                    .expectError(FieldSizeOutOfBoundsException.class)
                    .verify();
        }

        @Test
        void save_shouldReturnSizeOutOfBoundsException_whenIdNumberIsTooLong() {
            String longString = "a".repeat(DefaultValues.MAX_LENGTH_ID_NUMBER+1);
            User invalidUser = validUser.idNumber(longString).build();
            StepVerifier.create(userUseCase.saveUser(invalidUser))
                    .expectError(FieldSizeOutOfBoundsException.class)
                    .verify();
        }

        @Test
        void save_shouldReturnSizeOutOfBoundsException_whenPhoneIsTooLong() {
            String longString = "a".repeat(DefaultValues.MAX_LENGTH_PHONE+1);
            User invalidUser = validUser.phone(longString).build();
            StepVerifier.create(userUseCase.saveUser(invalidUser))
                    .expectError(FieldSizeOutOfBoundsException.class)
                    .verify();
        }

        @Test
        void save_shouldReturnSizeOutOfBoundsException_whenAddressIsTooLong() {
            String longString = "a".repeat(DefaultValues.MAX_LENGTH_ADDRESS+1);
            User invalidUser = validUser.address(longString).build();
            StepVerifier.create(userUseCase.saveUser(invalidUser))
                    .expectError(FieldSizeOutOfBoundsException.class)
                    .verify();
        }
    }

    @Nested
    @DisplayName("Repository Failure Tests")
    class RepositoryFailureTests {

        @Test
        void save_shouldReturnRolNotFoundException_whenRolDoesNotExist() {

            User userToSave = validUser.role(clientRole).build();
            when(roleRepository.findOne(clientRole)).thenReturn(Mono.empty());

            StepVerifier.create(userUseCase.saveUser(userToSave))
                    .expectError(RoleNotFoundException.class)
                    .verify();
        }

        @Test
        void save_shouldReturnEmailTakenException_whenEmailAlreadyExists() {

            User userToSave = validUser.role(clientRole).build();
            when(roleRepository.findOne(clientRole)).thenReturn(Mono.just(clientRole));
            when(userRepository.exists(any(User.class))).thenReturn(Mono.just(true));

            StepVerifier.create(userUseCase.saveUser(userToSave))
                    .expectError(EmailTakenException.class)
                    .verify();
        }
    }
}