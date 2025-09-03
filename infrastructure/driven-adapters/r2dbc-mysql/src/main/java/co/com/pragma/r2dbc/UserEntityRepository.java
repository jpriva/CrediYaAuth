package co.com.pragma.r2dbc;

import co.com.pragma.r2dbc.entity.UserEntity;
import org.springframework.data.repository.query.ReactiveQueryByExampleExecutor;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

import java.util.List;

public interface UserEntityRepository extends
        ReactiveCrudRepository<UserEntity, Integer>,
        ReactiveQueryByExampleExecutor<UserEntity> {

    Flux<UserEntity> findAllByEmailIn(List<String> emails);
}
