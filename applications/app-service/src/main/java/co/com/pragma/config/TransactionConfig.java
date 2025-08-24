package co.com.pragma.config;

import co.com.pragma.model.transaction.gateways.TransactionalPort;
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
    public TransactionalOperator transactionalOperator(ReactiveTransactionManager transactionManager) {
        return TransactionalOperator.create(transactionManager);
    }

    @Bean
    public TransactionalPort transactionalPort(TransactionalOperator transactionalOperator){
        return new TransactionalPort() {
            @Override
            public <T> Mono<T> transactional(Mono<T> mono) {
                return mono.as(transactionalOperator::transactional);
            }
        };
    }
}