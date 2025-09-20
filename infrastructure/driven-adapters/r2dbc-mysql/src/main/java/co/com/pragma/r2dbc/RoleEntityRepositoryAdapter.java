package co.com.pragma.r2dbc;

import co.com.pragma.model.role.Role;
import co.com.pragma.model.role.gateways.RoleRepository;
import co.com.pragma.r2dbc.mapper.PersistenceRoleMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
@RequiredArgsConstructor
public class RoleEntityRepositoryAdapter implements RoleRepository {

    private final RoleEntityRepository repository;
    private final PersistenceRoleMapper roleMapper;

    @Override
    public Mono<Role> findOne(Role role) {
        return repository.findOne(Example.of(roleMapper.toEntity(role)))
                .map(roleMapper::toDomain);
    }

    @Override
    public Mono<Role> findById(Integer roleId) {
        return repository.findById(roleId).map(roleMapper::toDomain);
    }
}
