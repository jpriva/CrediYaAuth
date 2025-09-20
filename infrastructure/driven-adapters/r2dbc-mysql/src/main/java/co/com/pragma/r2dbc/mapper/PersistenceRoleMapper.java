package co.com.pragma.r2dbc.mapper;

import co.com.pragma.model.role.Role;
import co.com.pragma.r2dbc.entity.RoleEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PersistenceRoleMapper {

    Role toDomain(RoleEntity roleEntity);

    RoleEntity toEntity(Role role);
}
