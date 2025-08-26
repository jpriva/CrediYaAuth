package co.com.pragma.r2dbc;

import co.com.pragma.model.user.User;
import co.com.pragma.model.user.exceptions.RoleNotFoundException;
import co.com.pragma.model.user.gateways.UserRepository;
import co.com.pragma.r2dbc.entity.UserEntity;
import co.com.pragma.r2dbc.helper.ReactiveAdapterOperations;
import co.com.pragma.r2dbc.mapper.UserMapper;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;


@Repository
public class UserRepositoryAdapter
        extends ReactiveAdapterOperations<User, UserEntity,Integer, UserEntityRepository>
        implements UserRepository
{

    private final RoleEntityRepository roleEntityRepository;

    public UserRepositoryAdapter(UserEntityRepository repository, ObjectMapper mapper, RoleEntityRepository roleEntityRepository) {
        super(repository, mapper, d -> mapper.map(d, User.class));
        this.roleEntityRepository = roleEntityRepository;
    }


    @Override
    public Mono<User> save(User user) {
        return roleEntityRepository.findById(user.getRole().getRolId())
                .switchIfEmpty(Mono.error(new RoleNotFoundException()))
                .flatMap(roleEntity -> repository.save(UserMapper.toEntity(user))
                        .map(savedUserEntity -> UserMapper.toData(savedUserEntity, roleEntity)));
    }

    @Override
    public Mono<Boolean> exists(User example) {
        return repository.exists(Example.of(UserMapper.toEntity(example)));
    }
}
