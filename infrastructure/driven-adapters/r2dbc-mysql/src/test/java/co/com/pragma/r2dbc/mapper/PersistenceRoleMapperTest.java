package co.com.pragma.r2dbc.mapper;

import co.com.pragma.model.role.Role;
import co.com.pragma.r2dbc.entity.RoleEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = PersistenceRoleMapperImpl.class)
class PersistenceRoleMapperTest {

    @Autowired
    private PersistenceRoleMapper roleMapper;

    @Nested
    @DisplayName("toDomain mapping tests")
    class ToDomain {

        @Test
        @DisplayName("should correctly map a valid RoleEntity to a Role domain object")
        void toDomain_whenValidEntity_shouldMapToDomain() {
            RoleEntity entity = RoleEntity.builder()
                    .rolId(1)
                    .name("Admin")
                    .description("Administrator role")
                    .build();

            Role domain = roleMapper.toDomain(entity);

            assertThat(domain).isNotNull();
            assertThat(domain.getRolId()).isEqualTo(entity.getRolId());
            assertThat(domain.getName()).isEqualTo(entity.getName());
            assertThat(domain.getDescription()).isEqualTo(entity.getDescription());
        }

        @Test
        @DisplayName("should return null when the input RoleEntity is null")
        void toDomain_whenEntityIsNull_shouldReturnNull() {
            Role domain = roleMapper.toDomain(null);

            assertThat(domain).isNull();
        }
    }
}
