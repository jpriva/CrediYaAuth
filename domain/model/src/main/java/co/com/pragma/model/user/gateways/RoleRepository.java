package co.com.pragma.model.user.gateways;

import co.com.pragma.model.user.Role;
import reactor.core.publisher.Mono;

public interface RoleRepository {
    Mono<Role> findOne(Role role);
}
