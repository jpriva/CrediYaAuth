package co.com.pragma.r2dbc;

import co.com.pragma.model.user.User;
import co.com.pragma.model.user.gateways.UserRepository;
import co.com.pragma.r2dbc.mapper.PersistenceRoleMapper;
import co.com.pragma.r2dbc.mapper.PersistenceUserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;


@Repository
@RequiredArgsConstructor
public class UserEntityRepositoryAdapter implements UserRepository {

    private final UserEntityRepository userRepository;
    private final RoleEntityRepository roleRepository;
    private final PersistenceUserMapper userMapper;
    private final PersistenceRoleMapper roleMapper;

    @Override
    public Mono<User> save(User user) {
        return userRepository.save(userMapper.toEntity(user))
                .map(savedEntity ->
                        user.toBuilder().userId(savedEntity.getUserId()).build()
                );
    }

    @Override
    public Mono<Boolean> exists(User example) {
        return userRepository.exists(Example.of(userMapper.toEntity(example)));
    }

    @Override
    public Mono<User> findOne(User example) {
        return userRepository.findOne(Example.of(userMapper.toEntity(example)))
                .map(userMapper::toDomain);
    }
}
