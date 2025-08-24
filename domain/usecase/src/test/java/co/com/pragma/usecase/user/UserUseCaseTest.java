package co.com.pragma.usecase.user;

import co.com.pragma.model.user.exceptions.SalaryUnboundException;
import co.com.pragma.model.user.User;
import co.com.pragma.model.user.exceptions.EmailFormatException;
import co.com.pragma.model.user.exceptions.EmailTakenException;
import co.com.pragma.model.user.exceptions.ErrorMessage;
import co.com.pragma.model.user.exceptions.UserFieldException;
import co.com.pragma.model.user.gateways.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;

@ExtendWith(MockitoExtension.class)
class UserUseCaseTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserUseCaseImpl userUseCase;

    @Test
    void saveShouldReturnErrorWhenUserIsNull() {
        StepVerifier.create(userUseCase.save(null))
                .expectErrorMatches(throwable -> throwable instanceof UserFieldException &&
                        throwable.getMessage().equals(ErrorMessage.REQUIRED_FIELDS))
                .verify();
    }

    @Test
    void saveShouldReturnErrorWhenRequiredFieldsAreMissing() {
        User user = User.builder().name("").lastName(null).email("").baseSalary(null).build();

        StepVerifier.create(userUseCase.save(user))
                .expectErrorMatches(throwable -> throwable instanceof UserFieldException &&
                        throwable.getMessage().equals(ErrorMessage.REQUIRED_FIELDS))
                .verify();
    }

    @Test
    void saveShouldReturnErrorWhenBaseSalaryIsNegative() {
        User user = User.builder()
                .name("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .baseSalary(new BigDecimal("-1"))
                .build();

        StepVerifier.create(userUseCase.save(user))
                .expectErrorMatches(throwable -> throwable instanceof SalaryUnboundException &&
                        throwable.getMessage().equals(ErrorMessage.SALARY_UNBOUND))
                .verify();
    }

    @Test
    void saveShouldReturnErrorWhenBaseSalaryExceedsLimit() {
        User user = User.builder()
                .name("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .baseSalary(new BigDecimal("20000000"))
                .build();

        StepVerifier.create(userUseCase.save(user))
                .expectErrorMatches(throwable -> throwable instanceof SalaryUnboundException &&
                        throwable.getMessage().equals(ErrorMessage.SALARY_UNBOUND))
                .verify();
    }

    @Test
    void saveShouldReturnErrorWhenEmailIsInvalid() {
        User user = User.builder()
                .name("John")
                .lastName("Doe")
                .email("invalid-email")
                .baseSalary(new BigDecimal("5000000"))
                .build();

        StepVerifier.create(userUseCase.save(user))
                .expectErrorMatches(throwable -> throwable instanceof EmailFormatException &&
                        throwable.getMessage().equals(ErrorMessage.EMAIL_FORMAT))
                .verify();
    }

    @Test
    void saveShouldReturnErrorWhenEmailIsAlreadyTaken() {
        User user = User.builder()
                .name("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .baseSalary(new BigDecimal("5000000"))
                .build();

        Mockito.when(userRepository.exists(Mockito.argThat(userArg ->
                        userArg != null && user.getEmail().equals(userArg.getEmail())
                )))
                .thenReturn(Mono.just(true));

        StepVerifier.create(userUseCase.save(user))
                .expectErrorMatches(throwable -> throwable instanceof EmailTakenException &&
                        throwable.getMessage().equals(ErrorMessage.EMAIL_TAKEN))
                .verify();
    }

    @Test
    void saveShouldSaveUserWhenAllFieldsAreValid() {
        User user = User.builder()
                .name("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .baseSalary(new BigDecimal("5000000"))
                .build();

        Mockito.when(userRepository.exists(Mockito.argThat(userArg ->
                        userArg != null && user.getEmail().equals(userArg.getEmail())
                )))
                .thenReturn(Mono.just(false));
        Mockito.when(userRepository.save(user)).thenReturn(Mono.just(user));

        StepVerifier.create(userUseCase.save(user))
                .expectNext(user)
                .verifyComplete();
    }
}
