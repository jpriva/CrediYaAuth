package co.com.pragma.startup;

import co.com.pragma.config.AdminUserProperties;
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

import static org.assertj.core.api.Assertions.assertThat;
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

    @Captor
    private ArgumentCaptor<User> userCaptor;

    private AdminUserInitializer adminUserInitializer;

    private ApplicationReadyEvent mockEvent;

    @BeforeEach
    void setUp() {
        adminUserInitializer = new AdminUserInitializer(userRepository, userUseCase, adminProps, logger);
        mockEvent = mock(ApplicationReadyEvent.class);
    }

    @Nested
    class OnApplicationEvent {

        @Test
        void whenAdminExists_shouldNotCreateNewUser() {
            when(adminProps.getEmail()).thenReturn("admin@crediya.com");
            when(adminProps.getPassword()).thenReturn("password");
            User existingAdmin = User.builder().userId(1).email("admin@crediya.com").build();
            when(userRepository.findOne(any(User.class))).thenReturn(Mono.just(existingAdmin));

            adminUserInitializer.onApplicationEvent(mockEvent);

            verify(userUseCase, never()).saveUser(any(User.class));
            verify(logger).info("Admin user check complete. Admin user ID: {}", 1);
        }

        @Test
        void whenAdminDoesNotExist_shouldCreateNewUser() {
            String adminEmail = "admin@crediya.com";
            String adminPassword = "DefaultAdminPassword123!";
            when(adminProps.getEmail()).thenReturn(adminEmail);
            when(adminProps.getPassword()).thenReturn(adminPassword);

            when(userRepository.findOne(any(User.class))).thenReturn(Mono.empty());

            User createdAdmin = User.builder().userId(1).email(adminEmail).build();
            when(userUseCase.saveUser(any(User.class))).thenReturn(Mono.just(createdAdmin));

            adminUserInitializer.onApplicationEvent(mockEvent);

            verify(userUseCase, times(1)).saveUser(userCaptor.capture());
            User capturedUser = userCaptor.getValue();

            assertThat(capturedUser.getEmail()).isEqualTo(adminEmail);
            assertThat(capturedUser.getPassword()).isEqualTo(adminPassword);
            assertThat(capturedUser.getName()).isEqualTo("Admin");
            assertThat(capturedUser.getRole().getName()).isEqualTo(DefaultValues.ADMIN_ROLE_NAME);

            verify(logger).info("Default admin user not found. Creating one...");
        }

        @Test
        void whenPropertiesAreMissing_shouldDoNothing() {

            adminUserInitializer.onApplicationEvent(mockEvent);

            verifyNoInteractions(userRepository, userUseCase);
            verify(logger).warn(anyString());
        }

        @Test
        void whenCreationFails_shouldLogError() {
            when(adminProps.getEmail()).thenReturn("admin@crediya.com");
            when(adminProps.getPassword()).thenReturn("password");

            when(userRepository.findOne(any(User.class))).thenReturn(Mono.empty());

            RuntimeException dbError = new RuntimeException("DB connection failed");
            when(userUseCase.saveUser(any(User.class))).thenReturn(Mono.error(dbError));

            adminUserInitializer.onApplicationEvent(mockEvent);

            verify(logger).error("Error during admin user initialization.", dbError);
        }
    }
}