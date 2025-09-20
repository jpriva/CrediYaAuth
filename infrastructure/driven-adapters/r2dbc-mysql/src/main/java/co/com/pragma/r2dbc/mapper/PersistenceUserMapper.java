package co.com.pragma.r2dbc.mapper;

import co.com.pragma.model.user.User;
import co.com.pragma.r2dbc.entity.UserEntity;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring",
        uses = { PersistenceRoleMapper.class },
        injectionStrategy = InjectionStrategy.CONSTRUCTOR
)
public interface PersistenceUserMapper {
    @Mapping(source = "rolId", target = "role.rolId")
    @Mapping(target = "role.name", ignore = true)
    @Mapping(target = "role.description", ignore = true)
    @Mapping(target = "password", ignore = true) // Never map password from DB to domain
    User toDomain(UserEntity userEntity);

    @Mapping(source = "role.rolId", target = "rolId")
    @Mapping(target = "password", source = "password") // Map password from domain to entity
    UserEntity toEntity(User user);
}