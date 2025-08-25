package co.com.pragma.api.mapper;

import co.com.pragma.api.dto.UserDTO;
import co.com.pragma.model.user.Role;
import co.com.pragma.model.user.User;

public class UserMapper {

    public static User toDomain(UserDTO dto) {
        return User.builder()
                .name(dto.getName())
                .lastName(dto.getLastName())
                .email(dto.getEmail())
                .idNumber(dto.getIdNumber())
                .role(Role.builder().rolId(dto.getRolId()).build())
                .baseSalary(dto.getBaseSalary())
                .build();
    }
}