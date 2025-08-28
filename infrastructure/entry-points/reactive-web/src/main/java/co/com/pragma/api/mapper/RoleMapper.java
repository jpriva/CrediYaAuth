package co.com.pragma.api.mapper;

import co.com.pragma.api.dto.RoleDTO;
import co.com.pragma.model.user.Role;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface RoleMapper {

    Role toDomain(RoleDTO dto);

    RoleDTO toResponseDto(Role role);

}