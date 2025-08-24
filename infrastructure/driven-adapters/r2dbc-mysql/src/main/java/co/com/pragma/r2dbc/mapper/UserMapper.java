package co.com.pragma.r2dbc.mapper;

import co.com.pragma.model.user.Role;
import co.com.pragma.model.user.User;
import co.com.pragma.r2dbc.entity.RoleEntity;
import co.com.pragma.r2dbc.entity.UserEntity;

public final class UserMapper {

    private UserMapper() {
    }

    public static User toData(UserEntity userEntity, RoleEntity roleEntity) {
        if (userEntity == null) {
            return null;
        }
        return User.builder()
                .userId(userEntity.getUserId())
                .name(userEntity.getName())
                .lastName(userEntity.getLastName())
                .email(userEntity.getEmail())
                .idNumber(userEntity.getIdNumber())
                .role(roleEntity == null ? null : Role.builder()
                        .rolId(roleEntity.getRolId())
                        .name(roleEntity.getName())
                        .description(roleEntity.getDescription())
                        .build())
                .baseSalary(userEntity.getBaseSalary())
                .phone(userEntity.getPhone())
                .address(userEntity.getAddress())
                .birthDate(userEntity.getBirthDate())
                .build();
    }

    public static UserEntity toEntity(User user) {
        if (user == null) {
            return null;
        }
        return UserEntity.builder()
                .userId(user.getUserId())
                .name(user.getName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .idNumber(user.getIdNumber())
                .rolId(user.getRole() == null ? null : user.getRole().getRolId())
                .baseSalary(user.getBaseSalary())
                .phone(user.getPhone())
                .address(user.getAddress())
                .birthDate(user.getBirthDate())
                .build();
    }
}
