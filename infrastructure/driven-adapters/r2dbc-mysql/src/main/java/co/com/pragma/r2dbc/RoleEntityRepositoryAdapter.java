package co.com.pragma.r2dbc;

import co.com.pragma.model.user.Role;
import co.com.pragma.model.user.gateways.RoleRepository;
import co.com.pragma.r2dbc.mapper.RoleMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
@RequiredArgsConstructor
public class RoleEntityRepositoryAdapter implements RoleRepository {

    private final RoleEntityRepository repository;
    private final RoleMapper roleMapper;

    @Override
    public Flux<Role> findAll() {
        return repository.findAll()
                .map(roleMapper::toDomain);
    }

    @Override
    public Mono<Role> findById(Integer id) {
        return repository.findById(id)
                .map(roleMapper::toDomain);
    }

    @Override
    public Mono<Role> findOne(Role role) {
        return repository.findOne(Example.of(roleMapper.toEntity(role)))
                .map(roleMapper::toDomain);
    }
}
