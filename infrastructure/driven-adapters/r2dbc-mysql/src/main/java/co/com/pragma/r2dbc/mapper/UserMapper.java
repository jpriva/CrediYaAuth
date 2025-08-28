package co.com.pragma.r2dbc.mapper;

import co.com.pragma.model.user.User;
import co.com.pragma.r2dbc.entity.UserEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = RoleMapper.class)
public interface UserMapper {

    @Mapping(source = "rolId", target = "role.rolId")
    User toDomain(UserEntity userEntity);

    @Mapping(source = "role.rolId", target = "rolId")
    UserEntity toEntity(User user);
}
