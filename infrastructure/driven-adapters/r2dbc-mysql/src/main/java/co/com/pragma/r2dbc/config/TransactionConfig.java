package co.com.pragma.r2dbc.config;

import co.com.pragma.model.transaction.gateways.TransactionalPort;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.ReactiveTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.reactive.TransactionalOperator;

@Configuration
@EnableTransactionManagement
public class TransactionConfig {

    @Bean
    public TransactionalPort transactionalPort(ReactiveTransactionManager transactionManager){
        return new TransactionalPort() {
            @Override
            public <T> Mono<T> transactional(Mono<T> mono) {
                TransactionalOperator transactionalOperator = TransactionalOperator.create(transactionManager);
                return transactionalOperator.transactional(mono);
            }

            @Override
            public <T> Flux<T> transactional(Flux<T> flux) {
                TransactionalOperator transactionalOperator = TransactionalOperator.create(transactionManager);
                return transactionalOperator.transactional(flux);
            }
        };
    }
}