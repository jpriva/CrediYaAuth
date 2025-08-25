package co.com.pragma;

import co.com.pragma.model.user.User;
import co.com.pragma.r2dbc.RoleEntityRepository;
import co.com.pragma.r2dbc.UserEntityRepository;
import co.com.pragma.r2dbc.UserRepositoryImpl;
import co.com.pragma.usecase.user.SaveUserUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;

@SpringBootTest
class UserUseCaseIntegrationTest {
    @Autowired
    private SaveUserUseCase saveUserUseCase;

    @Autowired
    private UserEntityRepository userEntityRepository;

    @MockitoSpyBean
    private UserRepositoryImpl userRepository;

    @BeforeEach
    void setup(){
        userEntityRepository.deleteAll().block();
    }

    @Test
    void shouldRollbackUserCreationWhenTransactionFails(){
        User userToSave = User.builder()
                .name("John").lastName("Doe")
                .email("john.doe@example.com")
                .baseSalary(new BigDecimal(2000000))
                .build();

        doReturn(Mono.error(new DataIntegrityViolationException("Simulated database error")))
                .when(userRepository).save(any(User.class));

        Mono<User> saveOperation = saveUserUseCase.execute(userToSave);

        StepVerifier.create(saveOperation)
                .expectError(DataIntegrityViolationException.class)
                .verify();
        StepVerifier.create(userEntityRepository.count()).expectNext(0L).verifyComplete();
    }
}
