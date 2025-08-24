package co.com.pragma.usecase.user;

import co.com.pragma.model.user.Role;
import co.com.pragma.model.user.User;
import co.com.pragma.model.user.exceptions.*;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserUseCaseImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @InjectMocks
    private UserUseCaseImpl userUseCase;

    private User.UserBuilder validUserBuilder;
    private Role clientRole;

    @BeforeEach
    void setUp() {
        clientRole = Role.builder().rolId(2).name("CLIENTE").build();
        validUserBuilder = User.builder()
                .name("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .baseSalary(new BigDecimal("5000000"));
    }

    @Test
    @DisplayName("save() should save user successfully when data is valid and role is provided")
    void save_shouldSaveUser_whenDataIsValidAndRolIsProvided() {
        // Arrange
        User userToSave = validUserBuilder.role(clientRole).build();
        User savedUser = userToSave.toBuilder().userId(1).build();

        when(roleRepository.findOne(clientRole)).thenReturn(Mono.just(clientRole));
        when(userRepository.exists(any(User.class))).thenReturn(Mono.just(false));
        when(userRepository.save(any(User.class))).thenReturn(Mono.just(savedUser));

        // Act & Assert
        StepVerifier.create(userUseCase.save(userToSave))
                .expectNext(savedUser)
                .verifyComplete();
    }

    @Test
    @DisplayName("save() should save user with default 'CLIENTE' role when no role is provided")
    void save_shouldSaveUserWithDefaultRol_whenRolIsNotProvided() {
        // Arrange
        User userWithoutRol = validUserBuilder.role(null).build();

        User userWithClientRol = userWithoutRol.toBuilder().role(clientRole).build();
        User savedUser = userWithClientRol.toBuilder().userId(1).build();

        when(roleRepository.findOne(any(Role.class))).thenReturn(Mono.just(clientRole));
        when(userRepository.exists(any(User.class))).thenReturn(Mono.just(false));
        when(userRepository.save(any(User.class))).thenReturn(Mono.just(savedUser));

        // Act & Assert
        StepVerifier.create(userUseCase.save(userWithoutRol))
                .expectNext(savedUser)
                .verifyComplete();
    }

    @Test
    @DisplayName("save() should save user with default 'CLIENTE' role when role object is provided but name is null")
    void save_shouldSaveUserWithDefaultRol_whenRolNameIsNull() {
        // Arrange
        User userWithEmptyRol = validUserBuilder.role(Role.builder().name(null).build()).build();

        User userWithClientRol = userWithEmptyRol.toBuilder().role(clientRole).build();
        User savedUser = userWithClientRol.toBuilder().userId(1).build();

        when(roleRepository.findOne(any(Role.class))).thenReturn(Mono.just(clientRole));
        when(userRepository.exists(any(User.class))).thenReturn(Mono.just(false));
        when(userRepository.save(any(User.class))).thenReturn(Mono.just(savedUser));

        // Act & Assert
        StepVerifier.create(userUseCase.save(userWithEmptyRol))
                .expectNext(savedUser)
                .verifyComplete();
    }

    @Nested
    @DisplayName("Validation Failure Tests")
    class ValidationFailureTests {

        @Test
        @DisplayName("save() should return UserFieldException when user is null")
        void save_shouldReturnUserFieldException_whenUserIsNull() {
            StepVerifier.create(userUseCase.save(null))
                    .expectError(UserFieldException.class)
                    .verify();
        }

        @Test
        @DisplayName("save() should return UserFieldException when required fields are blank")
        void save_shouldReturnUserFieldException_whenRequiredFieldsAreBlank() {
            User invalidUser = validUserBuilder.name("  ").build();
            StepVerifier.create(userUseCase.save(invalidUser))
                    .expectError(UserFieldException.class)
                    .verify();
        }

        @Test
        @DisplayName("save() should return SalaryUnboundException when salary is negative")
        void save_shouldReturnSalaryUnboundException_whenSalaryIsNegative() {
            User invalidUser = validUserBuilder.baseSalary(new BigDecimal("-100")).build();
            StepVerifier.create(userUseCase.save(invalidUser))
                    .expectError(SalaryUnboundException.class)
                    .verify();
        }

        @Test
        @DisplayName("save() should return SalaryUnboundException when salary is over the limit")
        void save_shouldReturnSalaryUnboundException_whenSalaryIsTooHigh() {
            User invalidUser = validUserBuilder.baseSalary(new BigDecimal("15000001")).build();
            StepVerifier.create(userUseCase.save(invalidUser))
                    .expectError(SalaryUnboundException.class)
                    .verify();
        }

        @Test
        @DisplayName("save() should return EmailFormatException when email format is invalid")
        void save_shouldReturnEmailFormatException_whenEmailIsInvalid() {
            User invalidUser = validUserBuilder.email("invalid-email.com").build();
            StepVerifier.create(userUseCase.save(invalidUser))
                    .expectError(EmailFormatException.class)
                    .verify();
        }
    }

    @Nested
    @DisplayName("Repository Failure Tests")
    class RepositoryFailureTests {

        @Test
        @DisplayName("save() should return RolNotFoundException when the role does not exist")
        void save_shouldReturnRolNotFoundException_whenRolDoesNotExist() {
            // Arrange
            User userToSave = validUserBuilder.role(clientRole).build();
            when(roleRepository.findOne(clientRole)).thenReturn(Mono.empty());

            // Act & Assert
            StepVerifier.create(userUseCase.save(userToSave))
                    .expectError(RoleNotFoundException.class)
                    .verify();
        }

        @Test
        @DisplayName("save() should return EmailTakenException when the email already exists")
        void save_shouldReturnEmailTakenException_whenEmailAlreadyExists() {
            // Arrange
            User userToSave = validUserBuilder.role(clientRole).build();
            when(roleRepository.findOne(clientRole)).thenReturn(Mono.just(clientRole));
            when(userRepository.exists(argThat(u -> u.getEmail().equals(userToSave.getEmail()))))
                    .thenReturn(Mono.just(true));

            // Act & Assert
            StepVerifier.create(userUseCase.save(userToSave))
                    .expectError(EmailTakenException.class)
                    .verify();
        }
    }
}