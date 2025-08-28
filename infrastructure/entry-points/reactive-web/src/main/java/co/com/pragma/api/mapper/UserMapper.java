package co.com.pragma.api.mapper;

import co.com.pragma.api.dto.UserRequestDTO;
import co.com.pragma.api.dto.UserResponseDTO;
import co.com.pragma.model.user.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = RoleMapper.class)
public interface UserMapper {

    @Mapping(target = "userId", ignore = true)
    User toDomain(UserRequestDTO dto);

    UserResponseDTO toResponseDto(User user);

}