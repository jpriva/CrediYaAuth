package co.com.pragma.startup;

import co.com.pragma.config.AdminUserProperties;
import co.com.pragma.model.constants.DefaultValues;
import co.com.pragma.model.logs.gateways.LoggerPort;
import co.com.pragma.model.role.Role;
import co.com.pragma.model.user.User;
import co.com.pragma.model.user.gateways.UserRepository;
import co.com.pragma.usecase.user.UserUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.util.StringUtils;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

@Component
@RequiredArgsConstructor
public class AdminUserInitializer implements ApplicationListener<ApplicationReadyEvent> {

    private final UserRepository userRepository;
    private final UserUseCase userUseCase;
    private final AdminUserProperties adminProps;
    private final LoggerPort logger;

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        createAdminUser();
    }

    private void createAdminUser() {
        if (adminProps == null || !StringUtils.hasText(adminProps.getEmail()) || !StringUtils.hasText(adminProps.getPassword())) {
            logger.warn("Default admin user properties (app.default-admin.email, app.default-admin.password) are not configured. Skipping admin user creation.");
            return;
        }

        User adminExample = User.builder().email(adminProps.getEmail()).build();

        userRepository.findOne(adminExample)
                .switchIfEmpty(Mono.defer(() -> {
                    logger.info("Default admin user not found. Creating one...");
                    User adminToCreate = User.builder()
                            .name("Admin")
                            .lastName("User")
                            .email(adminProps.getEmail())
                            .password(adminProps.getPassword())
                            .idNumber("00000000")
                            .baseSalary(BigDecimal.ZERO)
                            .role(Role.builder().name(DefaultValues.ADMIN_ROLE_NAME).build())
                            .build();
                    return userUseCase.saveUser(adminToCreate);
                }))
                .doOnSuccess(user -> logger.info("Admin user check complete. Admin user ID: {}", user.getUserId()))
                .then()
                .subscribe(null,
                        error -> logger.error("Error during admin user initialization.", error),
                        () -> logger.info("Admin user initialization process finished.")
                );
    }
}
