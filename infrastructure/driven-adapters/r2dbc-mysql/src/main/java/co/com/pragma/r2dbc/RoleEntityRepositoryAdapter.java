package co.com.pragma.r2dbc;

import co.com.pragma.model.user.Role;
import co.com.pragma.model.user.gateways.RoleRepository;
import co.com.pragma.r2dbc.entity.RoleEntity;
import co.com.pragma.r2dbc.helper.ReactiveAdapterOperations;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public class RoleEntityRepositoryAdapter
        extends ReactiveAdapterOperations<Role, RoleEntity,Integer, RoleEntityRepository>
        implements RoleRepository
{
    public RoleEntityRepositoryAdapter(RoleEntityRepository repository, ObjectMapper mapper) {
        super(repository, mapper, d -> mapper.map(d, Role.class));
    }

    @Override
    public Mono<Role> findOne(Role role) {
        return repository.findOne(Example.of(toData(role))).map(this::toEntity);
    }
}
