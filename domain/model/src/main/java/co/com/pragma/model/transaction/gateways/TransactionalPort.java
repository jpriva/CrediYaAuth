package co.com.pragma.model.transaction.gateways;

import reactor.core.publisher.Mono;

public interface TransactionalPort {
    <T> Mono<T> transactional(Mono<T> mono);
}