package co.com.pragma.api.mapper;

import co.com.pragma.api.dto.UserSaveRequestDTO;
import co.com.pragma.api.dto.UserResponseDTO;
import co.com.pragma.model.user.Role;
import co.com.pragma.model.user.User;

public class UserMapper {

    private UserMapper(){}

    public static User toDomain(UserSaveRequestDTO dto) {
        return User.builder()
                .name(dto.getName())
                .lastName(dto.getLastName())
                .email(dto.getEmail())
                .idNumber(dto.getIdNumber())
                .role(Role.builder().rolId(dto.getRolId()).build())
                .baseSalary(dto.getBaseSalary())
                .phone(dto.getPhone())
                .address(dto.getAddress())
                .birthDate(dto.getBirthDate())
                .build();
    }
    public static UserResponseDTO toResponseDto(User user) {
        return UserResponseDTO.builder()
                .userId(user.getUserId())
                .name(user.getName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .idNumber(user.getIdNumber())
                .roleId(user.getRole()!=null?user.getRole().getRolId():null)
                .roleName(user.getRole()!=null?user.getRole().getName():null)
                .roleDescription(user.getRole()!=null?user.getRole().getDescription():null)
                .baseSalary(user.getBaseSalary())
                .phone(user.getPhone())
                .address(user.getAddress())
                .birthDate(user.getBirthDate())
                .build();
    }
}