package co.com.pragma.r2dbc;

import co.com.pragma.model.user.User;
import co.com.pragma.model.user.filters.UserFilter;
import co.com.pragma.model.user.gateways.UserRepository;
import co.com.pragma.r2dbc.entity.UserEntity;
import co.com.pragma.r2dbc.mapper.PersistenceUserMapper;
import co.com.pragma.r2dbc.util.EntityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Example;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.relational.core.query.Query;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;


@Repository
@RequiredArgsConstructor
public class UserEntityRepositoryAdapter implements UserRepository {

    private final UserEntityRepository userRepository;
    private final PersistenceUserMapper userMapper;
    private final R2dbcEntityTemplate entityTemplate;

    @Override
    public Mono<User> save(User user) {
        return userRepository.save(userMapper.toEntity(user))
                .map(userMapper::toDomain)
                .map(savedDomainUser ->
                        savedDomainUser.toBuilder()
                                .role(user.getRole())
                                .build()
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

    @Override
    public Mono<User> findWithPasswordByEmail(String email) {
        return userRepository.findByEmail(email)
                .map(entity -> userMapper.toDomain(entity).toBuilder().password(entity.getPassword()).build());
    }

    @Override
    public Flux<User> findAllByEmail(List<String> emails) {
        return userRepository.findAllByEmailIn(emails).map(userMapper::toDomain);
    }

    @Override
    public Flux<User> findAllByRole(Integer roleId) {
        return userRepository.findAllByRolId(roleId).map(userMapper::toDomain);
    }

    @Override
    public Flux<User> findUsersByFilter(UserFilter filter) {
        Criteria criteria = EntityUtils.buildCriteria(filter);

        return entityTemplate.select(UserEntity.class)
                .from(EntityUtils.USER_TABLE_NAME)
                .matching(Query.query(criteria))
                .all()
                .map(userMapper::toDomain);
    }

    @Override
    public Flux<String> findUserEmailsByFilter(UserFilter filter) {
        Criteria criteria = EntityUtils.buildCriteria(filter);

        return entityTemplate.select(UserEntity.class)
                .from(EntityUtils.USER_TABLE_NAME)
                .matching(Query.query(criteria).columns(EntityUtils.EMAIL_COLUMN_NAME))
                .all()
                .map(UserEntity::getEmail);
    }

    @Override
    public Mono<User> findByEmail(String email) {
        return userRepository.findByEmail(email).map(userMapper::toDomain);
    }
}
