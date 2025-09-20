package co.com.pragma.startup;

import co.com.pragma.config.AdminUserProperties;
import co.com.pragma.config.SuperUserProperties;
import co.com.pragma.model.constants.DefaultValues;
import co.com.pragma.model.logs.gateways.LoggerPort;
import co.com.pragma.model.user.User;
import co.com.pragma.model.user.gateways.UserRepository;
import co.com.pragma.usecase.user.UserUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdminUserInitializerTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private UserUseCase userUseCase;
    @Mock
    private AdminUserProperties adminProps;
    @Mock
    private LoggerPort logger;
    @Mock
    private SuperUserProperties superProps;

    @Captor
    private ArgumentCaptor<User> userCaptor;

    private AdminUserInitializer adminUserInitializer;

    private ApplicationReadyEvent mockEvent;

    @BeforeEach
    void setUp() {
        adminUserInitializer = new AdminUserInitializer(userRepository, userUseCase, adminProps, superProps, logger);
        mockEvent = mock(ApplicationReadyEvent.class);
    }

    @Nested
    class OnApplicationEvent {

        @Test
        void whenAdminExists_shouldNotCreateNewUser() {
            when(superProps.getEmail()).thenReturn("super@crediya.com");
            when(superProps.getPassword()).thenReturn("password");
            when(adminProps.getEmail()).thenReturn("admin@crediya.com");
            when(adminProps.getPassword()).thenReturn("password");

            User existingSuper = User.builder().userId(0).email("super@crediya.com").build();
            User existingAdmin = User.builder().userId(1).email("admin@crediya.com").build();
            when(userRepository.findOne(any(User.class)))
                    .thenReturn(Mono.just(existingSuper))
                    .thenReturn(Mono.just(existingAdmin));

            adminUserInitializer.onApplicationEvent(mockEvent);

            verify(userUseCase, never()).saveUser(any(User.class));
            verify(logger).info("{} user check/creation complete. User ID: {}", DefaultValues.SUPER_USER_ROLE_NAME, 0);
            verify(logger).info("{} user check/creation complete. User ID: {}", DefaultValues.ADMIN_ROLE_NAME, 1);
        }

        @Test
        void whenAdminDoesNotExist_shouldCreateNewUser() {
            User.UserBuilder userBuilder = User.builder().name("User").lastName("NoLastName").baseSalary(BigDecimal.ZERO);
            String adminEmail = "admin@crediya.com";
            String adminPassword = "DefaultAdminPassword123!";
            String superEmail = "super@crediya.com";
            String superPassword = "DefaultSuperPassword123!";
            when(adminProps.getEmail()).thenReturn(adminEmail);
            when(adminProps.getPassword()).thenReturn(adminPassword);
            when(superProps.getEmail()).thenReturn(superEmail);
            when(superProps.getPassword()).thenReturn(superPassword);

            when(userRepository.findOne(any(User.class))).thenReturn(Mono.empty());

            User createdSuperUser = userBuilder.userId(1).email(superEmail).build();
            User createdAdminUser = userBuilder.userId(2).email(adminEmail).build();
            when(userUseCase.saveUser(any(User.class)))
                    .thenReturn(Mono.just(createdSuperUser))
                    .thenReturn(Mono.just(createdAdminUser));

            adminUserInitializer.onApplicationEvent(mockEvent);

            verify(userUseCase, times(2)).saveUser(userCaptor.capture());
            List<User> capturedUsers = userCaptor.getAllValues();
            User capturedSuperUser = capturedUsers.get(0);
            User capturedAdminUser = capturedUsers.get(1);

            assertThat(capturedSuperUser.getEmail()).isEqualTo(superEmail);
            assertThat(capturedSuperUser.getRole().getName()).isEqualTo(DefaultValues.SUPER_USER_ROLE_NAME);

            assertThat(capturedAdminUser.getEmail()).isEqualTo(adminEmail);
            assertThat(capturedAdminUser.getPassword()).isEqualTo(adminPassword);
            assertThat(capturedAdminUser.getName()).isEqualTo("User");
            assertThat(capturedAdminUser.getRole().getName()).isEqualTo(DefaultValues.ADMIN_ROLE_NAME);

            verify(logger).info("Default {} not found. Creating one...", DefaultValues.SUPER_USER_ROLE_NAME);
            verify(logger).info("Default {} not found. Creating one...", DefaultValues.ADMIN_ROLE_NAME);

            verify(logger).info("{} user check/creation complete. User ID: {}", DefaultValues.SUPER_USER_ROLE_NAME, 1);
            verify(logger).info("{} user check/creation complete. User ID: {}", DefaultValues.ADMIN_ROLE_NAME, 2);
        }

        @Test
        void whenPropertiesAreMissing_shouldDoNothing() {

            adminUserInitializer.onApplicationEvent(mockEvent);

            verifyNoInteractions(userRepository, userUseCase);
            verify(logger).warn("Default {} properties are not configured. Skipping {} creation.", DefaultValues.SUPER_USER_ROLE_NAME, DefaultValues.SUPER_USER_ROLE_NAME);
            verify(logger).warn("Default {} properties are not configured. Skipping {} creation.", DefaultValues.ADMIN_ROLE_NAME, DefaultValues.ADMIN_ROLE_NAME);
        }

        @Test
        void whenCreationFails_shouldLogError() {
            when(adminProps.getEmail()).thenReturn("admin@crediya.com");
            when(adminProps.getPassword()).thenReturn("password");

            when(userRepository.findOne(any(User.class))).thenReturn(Mono.empty());

            RuntimeException dbError = new RuntimeException("DB connection failed");
            when(userUseCase.saveUser(any(User.class))).thenReturn(Mono.error(dbError));

            assertThrows(RuntimeException.class,
                    () -> adminUserInitializer.onApplicationEvent(mockEvent),
                    "DB connection failed");

            verify(logger).warn("Default {} properties are not configured. Skipping {} creation.", DefaultValues.SUPER_USER_ROLE_NAME, DefaultValues.SUPER_USER_ROLE_NAME);
            verify(logger).error("Error during {} initialization.", DefaultValues.ADMIN_ROLE_NAME, dbError);
        }
    }
}