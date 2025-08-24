package co.com.pragma.r2dbc;

import co.com.pragma.model.user.User;
import co.com.pragma.model.user.gateways.UserRepository;
import co.com.pragma.r2dbc.entity.UserEntity;
import co.com.pragma.r2dbc.helper.ReactiveAdapterOperations;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public class UserEntityRepositoryAdapter extends ReactiveAdapterOperations<User,UserEntity,Integer,UserEntityRepository> implements UserRepository {
    public UserEntityRepositoryAdapter(UserEntityRepository repository, ObjectMapper mapper) {
        super(repository, mapper, d -> mapper.map(d, User.class));
    }

    @Override
    public Mono<Boolean> exists(User example) {
        return repository.exists(Example.of(mapper.map(example, UserEntity.class)));
    }
}
