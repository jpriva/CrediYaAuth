package co.com.pragma.r2dbc;

import co.com.pragma.r2dbc.entity.RoleEntity;
import org.springframework.data.repository.query.ReactiveQueryByExampleExecutor;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface RoleEntityRepository extends
        ReactiveCrudRepository<RoleEntity, Integer>,
        ReactiveQueryByExampleExecutor<RoleEntity> {
}
