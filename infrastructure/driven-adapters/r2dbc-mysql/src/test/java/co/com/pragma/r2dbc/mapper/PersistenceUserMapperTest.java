package co.com.pragma.r2dbc.mapper;

import co.com.pragma.model.user.User;
import co.com.pragma.r2dbc.entity.UserEntity;
import org.junit.jupiter.api.DisplayName;
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
    @DisplayName("toDomain mapping tests")
    class ToDomain {

        @Test
        @DisplayName("should correctly map a valid UserEntity to a User domain object")
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
        @DisplayName("should return null when the input UserEntity is null")
        void toDomain_whenEntityIsNull_shouldReturnNull() {
            User domain = userMapper.toDomain(null);

            assertThat(domain).isNull();
        }
    }
}
