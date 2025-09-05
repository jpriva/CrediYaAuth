package co.com.pragma.r2dbc;

import co.com.pragma.model.role.Role;
import co.com.pragma.model.user.User;
import co.com.pragma.model.user.filters.UserFilter;
import co.com.pragma.r2dbc.entity.UserEntity;
import co.com.pragma.r2dbc.mapper.PersistenceUserMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Answers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.relational.core.query.Query;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserEntityRepositoryAdapterTest {

    @Mock
    private UserEntityRepository userRepository;
    @Mock
    private PersistenceUserMapper userMapper;
    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private R2dbcEntityTemplate entityTemplate;

    @InjectMocks
    private UserEntityRepositoryAdapter adapter;

    private User userDomain;
    private UserEntity userEntity;

    @BeforeEach
    void setUp() {
        userDomain = User.builder().userId(1).email("test@example.com").build();
        userEntity = new UserEntity();
        userEntity.setUserId(1);
        userEntity.setEmail("test@example.com");
    }

    @Test
    void save_shouldSaveAndReturnUser() {
        Role fullRole = Role.builder().rolId(1).name("ADMIN").description("Administrator").build();
        User userToSave = User.builder().email("test@example.com").role(fullRole).build();

        UserEntity savedEntity = new UserEntity();
        savedEntity.setUserId(99);
        savedEntity.setEmail("test@example.com");
        savedEntity.setRolId(1);

        User mappedFromDb = User.builder().userId(99).email("test@example.com").role(Role.builder().rolId(1).name("ADMIN").description("Administrator").build()).build();

        when(userMapper.toEntity(userToSave)).thenReturn(savedEntity);
        when(userRepository.save(savedEntity)).thenReturn(Mono.just(savedEntity));
        when(userMapper.toDomain(savedEntity)).thenReturn(mappedFromDb);

        StepVerifier.create(adapter.save(userToSave))
                // Assert
                .assertNext(finalUser -> {
                    assertThat(finalUser.getUserId()).isEqualTo(99);
                    assertThat(finalUser.getRole()).isNotNull();
                    assertThat(finalUser.getRole().getName()).isEqualTo("ADMIN");
                    assertThat(finalUser.getRole().getDescription()).isEqualTo("Administrator");
                })
                .verifyComplete();
    }

    @Test
    void exists_shouldReturnTrue() {
        when(userMapper.toEntity(any(User.class))).thenReturn(userEntity);
        when(userRepository.exists(any())).thenReturn(Mono.just(true));

        StepVerifier.create(adapter.exists(userDomain))
                .expectNext(true)
                .verifyComplete();
    }

    @Test
    void findOne_shouldReturnUser() {
        when(userMapper.toEntity(any(User.class))).thenReturn(userEntity);
        when(userRepository.findOne(any())).thenReturn(Mono.just(userEntity));
        when(userMapper.toDomain(any(UserEntity.class))).thenReturn(userDomain);

        StepVerifier.create(adapter.findOne(userDomain))
                .expectNext(userDomain)
                .verifyComplete();
    }

    @Test
    void findWithPasswordByEmail_shouldReturnUserWithPassword() {
        userEntity.setPassword("hashed_password");
        when(userRepository.findByEmail(anyString())).thenReturn(Mono.just(userEntity));
        when(userMapper.toDomain(any(UserEntity.class))).thenReturn(userDomain);

        StepVerifier.create(adapter.findWithPasswordByEmail("test@example.com"))
                .expectNextMatches(user -> "hashed_password".equals(user.getPassword()))
                .verifyComplete();
    }

    @Test
    void findAllByEmail_shouldReturnFluxOfUsers() {
        List<String> emails = List.of("test@example.com");
        when(userRepository.findAllByEmailIn(emails)).thenReturn(Flux.just(userEntity));
        when(userMapper.toDomain(userEntity)).thenReturn(userDomain);

        StepVerifier.create(adapter.findAllByEmail(emails))
                .expectNext(userDomain)
                .verifyComplete();
    }

    @Test
    void findByEmail_shouldReturnUser() {
        when(userRepository.findByEmail(anyString())).thenReturn(Mono.just(userEntity));
        when(userMapper.toDomain(userEntity)).thenReturn(userDomain);

        StepVerifier.create(adapter.findByEmail("test@example.com"))
                .expectNext(userDomain)
                .verifyComplete();
    }

    @Nested
    class FindUsersByFilter {
        @Test
        void findUsersByFilter_shouldReturnMatchingUsers() {
            UserFilter filter = UserFilter.builder().name("test").build();
            when(entityTemplate.select(UserEntity.class)
                    .from(anyString())
                    .matching(any(Query.class))
                    .all())
                    .thenReturn(Flux.just(userEntity));
            when(userMapper.toDomain(userEntity)).thenReturn(userDomain);

            StepVerifier.create(adapter.findUsersByFilter(filter))
                    .expectNext(userDomain)
                    .verifyComplete();

        }

        @Test
        @DisplayName("should return an empty flux when no users match the filter")
        void findUsersByFilter_shouldReturnEmptyWhenNoMatch() {
            UserFilter filter = UserFilter.builder().name("no-match").build();
            when(entityTemplate.select(UserEntity.class)
                    .from(anyString())
                    .matching(any(Query.class))
                    .all())
                    .thenReturn(Flux.empty());

            StepVerifier.create(adapter.findUsersByFilter(filter))
                    .verifyComplete();
        }
    }

    @Nested
    class FindUserEmailsByFilter {
        @Test
        void findUserEmailsByFilter_shouldReturnMatchingEmails() {
            UserFilter filter = UserFilter.builder().name("test").build();
            userEntity.setEmail("filtered@example.com");
            when(entityTemplate.select(UserEntity.class)
                    .from(anyString())
                    .matching(any(Query.class))
                    .all())
                    .thenReturn(Flux.just(userEntity));

            StepVerifier.create(adapter.findUserEmailsByFilter(filter))
                    .expectNext("filtered@example.com")
                    .verifyComplete();

        }

        @Test
        void findUserEmailsByFilter_shouldReturnEmptyWhenNoMatch() {
            UserFilter filter = UserFilter.builder().name("no-match").build();
            when(entityTemplate.select(UserEntity.class)
                    .from(anyString())
                    .matching(any(Query.class))
                    .all())
                    .thenReturn(Flux.empty());

            StepVerifier.create(adapter.findUserEmailsByFilter(filter))
                    .verifyComplete();
        }
    }
}
