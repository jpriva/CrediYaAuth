package co.com.pragma.r2dbc.config;

import co.com.pragma.model.transaction.gateways.TransactionalPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.ReactiveTransaction;
import org.springframework.transaction.ReactiveTransactionManager;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionConfigTest {

    @Mock
    private ReactiveTransactionManager mockTransactionManager;

    @Mock
    private ReactiveTransaction mockTransaction;

    private TransactionalPort transactionalPort;

    @BeforeEach
    void setUp() {
        TransactionConfig transactionConfig = new TransactionConfig();
        transactionalPort = transactionConfig.transactionalPort(mockTransactionManager);

        lenient().when(mockTransactionManager.getReactiveTransaction(any())).thenReturn(Mono.just(mockTransaction));
        lenient().when(mockTransactionManager.commit(any())).thenReturn(Mono.empty());
        lenient().when(mockTransactionManager.rollback(any())).thenReturn(Mono.empty());
    }

    @Nested
    @DisplayName("Mono Transactional Tests")
    class MonoTests {
        @Test
        @DisplayName("should commit transaction on successful Mono completion")
        void transactionalMono_shouldCommitOnSuccess() {
            Mono<String> successfulMono = Mono.just("Success");

            Mono<String> transactionalMono = transactionalPort.transactional(successfulMono);

            StepVerifier.create(transactionalMono)
                    .expectNext("Success")
                    .verifyComplete();

            verify(mockTransactionManager, times(1)).commit(any());
            verify(mockTransactionManager, never()).rollback(any());
        }

        @Test
        @DisplayName("should rollback transaction on Mono error")
        void transactionalMono_shouldRollbackOnError() {
            RuntimeException testException = new RuntimeException("Test Exception");
            Mono<String> failingMono = Mono.error(testException);

            Mono<String> transactionalMono = transactionalPort.transactional(failingMono);

            StepVerifier.create(transactionalMono)
                    .expectErrorMatches(error -> error.equals(testException))
                    .verify();

            verify(mockTransactionManager, times(1)).rollback(any());
            verify(mockTransactionManager, never()).commit(any());
        }
    }

    @Nested
    @DisplayName("Flux Transactional Tests")
    class FluxTests {
        @Test
        @DisplayName("should commit transaction on successful Flux completion")
        void transactionalFlux_shouldCommitOnSuccess() {
            Flux<Integer> successfulFlux = Flux.just(1, 2, 3);

            Flux<Integer> transactionalFlux = transactionalPort.transactional(successfulFlux);

            StepVerifier.create(transactionalFlux)
                    .expectNext(1, 2, 3)
                    .verifyComplete();

            verify(mockTransactionManager, times(1)).commit(any());
            verify(mockTransactionManager, never()).rollback(any());
        }

        @Test
        @DisplayName("should rollback transaction on Flux error")
        void transactionalFlux_shouldRollbackOnError() {
            RuntimeException testException = new RuntimeException("Test Exception");
            Flux<Integer> failingFlux = Flux.concat(Flux.just(1, 2), Mono.error(testException));

            Flux<Integer> transactionalFlux = transactionalPort.transactional(failingFlux);

            StepVerifier.create(transactionalFlux)
                    .expectNext(1, 2)
                    .expectErrorMatches(error -> error.equals(testException))
                    .verify();

            verify(mockTransactionManager, times(1)).rollback(any());
            verify(mockTransactionManager, never()).commit(any());
        }
    }
}