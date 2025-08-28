package co.com.pragma.r2dbc;

import co.com.pragma.model.constants.DefaultValues;
import co.com.pragma.model.exceptions.RoleNotFoundException;
import co.com.pragma.model.user.Role;
import co.com.pragma.model.user.User;
import co.com.pragma.r2dbc.entity.RoleEntity;
import co.com.pragma.r2dbc.entity.UserEntity;
import co.com.pragma.r2dbc.mapper.PersistenceRoleMapper;
import co.com.pragma.r2dbc.mapper.PersistenceUserMapper;
import io.r2dbc.spi.ConnectionFactories;
import io.r2dbc.spi.ConnectionFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Example;
import org.springframework.data.r2dbc.config.AbstractR2dbcConfiguration;
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories;
import org.springframework.lang.NonNull;
import org.springframework.r2dbc.connection.init.ConnectionFactoryInitializer;
import org.springframework.r2dbc.connection.init.ResourceDatabasePopulator;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = UserEntityRepositoryAdapterIntegrationTest.TestConfig.class)
class UserEntityRepositoryAdapterIntegrationTest {

    @Configuration
    @EnableR2dbcRepositories(basePackages = "co.com.pragma.r2dbc")
    @ComponentScan(basePackages = "co.com.pragma.r2dbc.mapper")
    static class TestConfig extends AbstractR2dbcConfiguration {

        @Override
        @Bean
        @NonNull
        public ConnectionFactory connectionFactory() {
            return ConnectionFactories.get("r2dbc:h2:mem:///testdb;DB_CLOSE_DELAY=-1;");
        }

        @Bean
        ConnectionFactoryInitializer initializer(ConnectionFactory connectionFactory) {
            ConnectionFactoryInitializer initializer = new ConnectionFactoryInitializer();
            initializer.setConnectionFactory(connectionFactory);
            initializer.setDatabasePopulator(new ResourceDatabasePopulator(new ClassPathResource("schema.sql")));
            return initializer;
        }

        @Bean
        public UserEntityRepositoryAdapter userRepositoryAdapter(
                UserEntityRepository userEntityRepository,
                RoleEntityRepository roleEntityRepository,
                PersistenceUserMapper userMapper,
                PersistenceRoleMapper roleMapper
        ) {
            return new UserEntityRepositoryAdapter(userEntityRepository, roleEntityRepository, userMapper, roleMapper);
        }
    }

    @Autowired
    private UserEntityRepositoryAdapter userRepository;

    @Autowired
    private UserEntityRepository userEntityRepository;

    @Autowired
    private RoleEntityRepository roleEntityRepository;

    private RoleEntity savedRoleEntity;
    private Role savedRole;

    @BeforeEach
    void setUp() {
        userEntityRepository.deleteAll().block();

        savedRoleEntity = roleEntityRepository.findOne(Example.of(RoleEntity.builder().rolId(DefaultValues.DEFAULT_ROLE_ID).build())).block();
        savedRole = Role.builder().rolId(savedRoleEntity.getRolId()).name(savedRoleEntity.getName()).description(savedRoleEntity.getDescription()).build();
    }

    @Test
    void save_shouldPersistAndReturnUser_whenSuccessful() {
        User userToSave = User.builder()
                .name("Jane")
                .lastName("Doe")
                .email("jane.doe@example.com")
                .idNumber("987654321")
                .role(savedRole)
                .baseSalary(new BigDecimal("60000"))
                .build();

        Mono<User> result = userRepository.save(userToSave);

        StepVerifier.create(result)
                .assertNext(savedUser -> {
                    assert savedUser.getUserId() != null;
                    assert savedUser.getName().equals(userToSave.getName());
                    assert savedUser.getRole() != null;
                    assert savedUser.getRole().getRolId().equals(savedRoleEntity.getRolId());
                    assert savedUser.getRole().getName().equals(savedRoleEntity.getName());
                })
                .verifyComplete();

        Mono<Long> countOperation = userEntityRepository.count(Example.of(UserEntity.builder().email("jane.doe@example.com").build()));

        StepVerifier.create(countOperation).expectNext(1L).verifyComplete();
    }

    @Test
    void save_shouldReturnError_whenRoleIsNotFound() {
        int nonExistentRoleId = 999;
        User userToSave = User.builder()
                .name("Jane")
                .lastName("Doe")
                .email("jane.doe@example.com")
                .idNumber("987654321")
                .role(Role.builder().rolId(nonExistentRoleId).build())
                .baseSalary(new BigDecimal("60000"))
                .build();

        Mono<User> result = userRepository.save(userToSave);

        StepVerifier.create(result)
                .expectError(RoleNotFoundException.class)
                .verify();
    }

    @Test
    void exists_shouldReturnTrue_whenUserExists() {
        userEntityRepository.save(UserEntity.builder()
                .email("exists@example.com")
                .name("a")
                .lastName("b")
                .idNumber("1")
                .rolId(savedRoleEntity.getRolId())
                .baseSalary(BigDecimal.ONE)
                .build()
        ).block();
        User example = User.builder().email("exists@example.com").build();

        Mono<Boolean> result = userRepository.exists(example);

        StepVerifier.create(result)
                .expectNext(true).verifyComplete();
    }

    @Test
    void exists_shouldReturnFalse_whenUserDoesNotExist() {
        User example = User.builder().email("nonexistent@example.com").build();

        Mono<Boolean> result = userRepository.exists(example);

        StepVerifier.create(result)
                .expectNext(false).verifyComplete();
    }
}