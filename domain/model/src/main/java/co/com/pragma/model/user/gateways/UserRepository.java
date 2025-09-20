package co.com.pragma.model.user.gateways;

import co.com.pragma.model.user.User;
import co.com.pragma.model.user.filters.UserFilter;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface UserRepository {

    Mono<User> save(User user);

    Mono<Boolean> exists(User example);

    Mono<User> findOne(User example);

    Mono<User> findWithPasswordByEmail(String email);

    Mono<User> findByEmail(String email);

    Flux<User> findAllByEmail(List<String> email);

    Flux<User> findUsersByFilter(UserFilter filter);

    Flux<String> findUserEmailsByFilter(UserFilter filter);
}
