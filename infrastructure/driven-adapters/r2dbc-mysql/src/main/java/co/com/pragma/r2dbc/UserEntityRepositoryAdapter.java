package co.com.pragma.r2dbc;

import co.com.pragma.model.user.User;
import co.com.pragma.model.user.exceptions.RoleNotFoundException;
import co.com.pragma.model.user.gateways.UserRepository;
import co.com.pragma.r2dbc.mapper.RoleMapper;
import co.com.pragma.r2dbc.mapper.UserMapper;
import co.com.pragma.r2dbc.util.UserUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@SuppressWarnings("java:S2209")
@Repository
@RequiredArgsConstructor
public class UserEntityRepositoryAdapter implements UserRepository {

    private final UserEntityRepository userRepository;
    private final RoleEntityRepository roleRepository;
    private final UserMapper userMapper;
    private final RoleMapper roleMapper;

    @Override
    public Mono<User> save(User user) {
        return roleRepository.findById(user.getRole().getRolId())
                .switchIfEmpty(Mono.defer(() -> Mono.error(new RoleNotFoundException())))
                .flatMap(roleEntity ->
                        userRepository.save(userMapper.toEntity(user))
                                .map(userEntity ->
                                        UserUtil.setUserRole(userMapper.toDomain(userEntity), roleMapper.toDomain(roleEntity))
                                )
                );
    }

    @Override
    public Mono<Boolean> exists(User example) {
        return userRepository.exists(Example.of(userMapper.toEntity(example)));
    }
}
