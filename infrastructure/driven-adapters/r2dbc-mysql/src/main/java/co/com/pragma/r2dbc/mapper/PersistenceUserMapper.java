package co.com.pragma.r2dbc.mapper;

import co.com.pragma.model.user.User;
import co.com.pragma.r2dbc.entity.UserEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = PersistenceRoleMapper.class)
public interface PersistenceUserMapper {
    @Mapping(source = "rolId", target = "role.rolId")
    @Mapping(target = "role.name", ignore = true)
    @Mapping(target = "role.description", ignore = true)
    User toDomain(UserEntity userEntity);

    @Mapping(source = "role.rolId", target = "rolId")
    UserEntity toEntity(User user);
}