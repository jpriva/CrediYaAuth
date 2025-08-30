package co.com.pragma.api.mapper;

import co.com.pragma.api.dto.RoleDTO;
import co.com.pragma.model.role.Role;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {RoleMapperImpl.class})
class RoleMapperTest {

    @Autowired
    private RoleMapper roleMapper;

    @Test
    @DisplayName("toResponseDto should correctly map a valid Role domain object to a DTO")
    void toResponseDto_shouldCorrectlyMapValidRole() {
        Role domainRole = Role.builder()
                .rolId(1)
                .name("ADMIN")
                .description("Administrator role with full permissions")
                .build();

        RoleDTO responseDTO = roleMapper.toResponseDto(domainRole);

        assertThat(responseDTO).isNotNull();
        assertThat(responseDTO.getRolId()).isEqualTo(domainRole.getRolId());
        assertThat(responseDTO.getName()).isEqualTo(domainRole.getName());
        assertThat(responseDTO.getDescription()).isEqualTo(domainRole.getDescription());
    }

    @Test
    @DisplayName("toResponseDto should return null when the input Role domain object is null")
    void toResponseDto_shouldReturnNullWhenRoleIsNull() {

        RoleDTO responseDTO = roleMapper.toResponseDto(null);

        assertThat(responseDTO).isNull();
    }
}