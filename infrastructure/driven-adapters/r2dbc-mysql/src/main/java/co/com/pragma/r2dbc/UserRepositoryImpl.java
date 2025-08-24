package co.com.pragma.r2dbc;

import co.com.pragma.model.user.User;
import co.com.pragma.model.user.exceptions.RoleNotFoundException;
import co.com.pragma.model.user.gateways.UserRepository;
import co.com.pragma.r2dbc.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
@RequiredArgsConstructor
public class UserRepositoryImpl implements
        UserRepository {

    private final UserEntityRepository userEntityRepository;
    private final RoleEntityRepository roleEntityRepository;

    @Override
    public Mono<User> save(User user) {
        return roleEntityRepository.findById(user.getRole().getRolId())
                .switchIfEmpty(Mono.error(new RoleNotFoundException()))
                .flatMap(roleEntity -> userEntityRepository.save(UserMapper.toEntity(user))
                        .map(savedUserEntity -> UserMapper.toData(savedUserEntity, roleEntity)));
    }

    @Override
    public Mono<Boolean> exists(User example) {
        return userEntityRepository.exists(Example.of(UserMapper.toEntity(example)));
    }
}
