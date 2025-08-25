package co.com.pragma.model.transaction.gateways;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface TransactionalPort {
    <T> Mono<T> transactional(Mono<T> mono);
    <T> Flux<T> transactional(Flux<T> flux);
}