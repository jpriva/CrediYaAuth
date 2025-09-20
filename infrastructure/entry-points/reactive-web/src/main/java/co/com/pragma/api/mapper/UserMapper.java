package co.com.pragma.api.mapper;

import co.com.pragma.api.dto.FindUsersRequestDTO;
import co.com.pragma.api.dto.UserRequestDTO;
import co.com.pragma.api.dto.UserResponseDTO;
import co.com.pragma.model.user.User;
import co.com.pragma.model.user.filters.UserFilter;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring",
        uses = {RoleMapper.class},
        injectionStrategy = InjectionStrategy.CONSTRUCTOR
)
public interface UserMapper {

    @Mapping(target = "userId", ignore = true)
    @Mapping(target = "role.rolId", source = "rolId")
    User toDomain(UserRequestDTO dto);

    UserResponseDTO toResponseDto(User user);

    UserFilter toUserFilter(FindUsersRequestDTO dto);
}