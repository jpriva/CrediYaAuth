package co.com.pragma.model.user.gateways;

import co.com.pragma.model.user.Role;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface RoleRepository {
    Flux<Role> findAll();
    Mono<Role> findById(Integer id);
    Mono<Role> findOne(Role role);
}
