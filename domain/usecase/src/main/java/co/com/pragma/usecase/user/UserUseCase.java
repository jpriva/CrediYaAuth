package co.com.pragma.usecase.user;

import co.com.pragma.model.user.User;
import reactor.core.publisher.Mono;

public interface UserUseCase {
    Mono<User> save(User user);
}
