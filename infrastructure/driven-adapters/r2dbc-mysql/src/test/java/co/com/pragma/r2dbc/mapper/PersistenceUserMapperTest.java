package co.com.pragma.r2dbc.mapper;

import co.com.pragma.model.role.Role;
import co.com.pragma.model.user.User;
import co.com.pragma.r2dbc.entity.UserEntity;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {PersistenceUserMapperImpl.class, PersistenceRoleMapperImpl.class})
class PersistenceUserMapperTest {

    @Autowired
    private PersistenceUserMapper userMapper;

    @Nested
    class ToDomain {

        @Test
        void toDomain_whenValidEntity_shouldMapToDomain() {
            UserEntity entity = UserEntity.builder()
                    .userId(1)
                    .name("John")
                    .lastName("Doe")
                    .email("john.doe@example.com")
                    .idNumber("123456789")
                    .rolId(10)
                    .baseSalary(new BigDecimal("50000.00"))
                    .phone("1234567890")
                    .address("123 Main St")
                    .birthDate(LocalDate.of(1990, 1, 1))
                    .build();

            User domain = userMapper.toDomain(entity);

            assertThat(domain).isNotNull();
            assertThat(domain.getUserId()).isEqualTo(entity.getUserId());
            assertThat(domain.getName()).isEqualTo(entity.getName());
            assertThat(domain.getLastName()).isEqualTo(entity.getLastName());
            assertThat(domain.getEmail()).isEqualTo(entity.getEmail());
            assertThat(domain.getIdNumber()).isEqualTo(entity.getIdNumber());
            assertThat(domain.getBaseSalary()).isEqualByComparingTo(entity.getBaseSalary());
            assertThat(domain.getPhone()).isEqualTo(entity.getPhone());
            assertThat(domain.getAddress()).isEqualTo(entity.getAddress());
            assertThat(domain.getBirthDate()).isEqualTo(entity.getBirthDate());
            assertThat(domain.getRole()).isNotNull();
            assertThat(domain.getRole().getRolId()).isEqualTo(entity.getRolId());
            assertThat(domain.getRole().getName()).isNull();
            assertThat(domain.getRole().getDescription()).isNull();
        }

        @Test
        void toDomain_whenEntityIsNull_shouldReturnNull() {
            User domain = userMapper.toDomain(null);

            assertThat(domain).isNull();
        }
    }

    @Nested
    class ToEntity {

        @Test
        void ToEntity_whenValidDomain_shouldMapToEntity() {
            User domain = User.builder()
                    .userId(1)
                    .name("John")
                    .lastName("Doe")
                    .email("john.doe@example.com")
                    .idNumber("123456789")
                    .role(Role.builder().rolId(10).build())
                    .baseSalary(new BigDecimal("50000.00"))
                    .phone("1234567890")
                    .address("123 Main St")
                    .birthDate(LocalDate.of(1990, 1, 1))
                    .build();

            UserEntity entity = userMapper.toEntity(domain);

            assertThat(entity).isNotNull();
            assertThat(entity.getUserId()).isEqualTo(domain.getUserId());
            assertThat(entity.getName()).isEqualTo(domain.getName());
            assertThat(entity.getLastName()).isEqualTo(domain.getLastName());
            assertThat(entity.getEmail()).isEqualTo(domain.getEmail());
            assertThat(entity.getIdNumber()).isEqualTo(domain.getIdNumber());
            assertThat(entity.getBaseSalary()).isEqualByComparingTo(domain.getBaseSalary());
            assertThat(entity.getPhone()).isEqualTo(domain.getPhone());
            assertThat(entity.getAddress()).isEqualTo(domain.getAddress());
            assertThat(entity.getBirthDate()).isEqualTo(domain.getBirthDate());
            assertThat(entity.getRolId()).isEqualTo(domain.getRole().getRolId());
        }

        @Test
        void ToEntity_whenDomainIsNull_shouldReturnNull() {
            UserEntity entity = userMapper.toEntity(null);

            assertThat(entity).isNull();
        }
    }
}
