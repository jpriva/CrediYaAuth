package co.com.pragma.startup;

import co.com.pragma.config.AdminUserProperties;
import co.com.pragma.config.SuperUserProperties;
import co.com.pragma.model.constants.DefaultValues;
import co.com.pragma.model.logs.gateways.LoggerPort;
import co.com.pragma.model.role.Role;
import co.com.pragma.model.user.User;
import co.com.pragma.model.user.gateways.UserRepository;
import co.com.pragma.usecase.user.UserUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

@Component
@RequiredArgsConstructor
public class AdminUserInitializer implements ApplicationListener<ApplicationReadyEvent> {

    private final UserRepository userRepository;
    private final UserUseCase userUseCase;
    private final AdminUserProperties adminProps;
    private final SuperUserProperties superProps;
    private final LoggerPort logger;

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        if ( superProps != null )
            createInitialUser(superProps.getEmail(),superProps.getPassword(), "000000", DefaultValues.SUPER_USER_ROLE_NAME);
        if ( adminProps != null )
            createInitialUser(adminProps.getEmail(),adminProps.getPassword(), "000001", DefaultValues.ADMIN_ROLE_NAME);
    }

    private void createInitialUser(String email, String password, String idNumber, String roleName) {
        if (!StringUtils.hasText(email) || !StringUtils.hasText(password)) {
            logger.warn("Default {} properties are not configured. Skipping {} creation.", roleName, roleName);
            return;
        }

        User userExample = User.builder().email(email).build();

        userRepository.findOne(userExample)
                .switchIfEmpty(Mono.defer(() -> {
                    logger.info("Default {} not found. Creating one...", roleName);
                    User adminToCreate = User.builder()
                            .name("User")
                            .lastName("NoLastName")
                            .email(email)
                            .password(password)
                            .idNumber(idNumber)
                            .baseSalary(BigDecimal.ZERO)
                            .role(Role.builder().name(roleName).build())
                            .build();
                    return userUseCase.saveUser(adminToCreate);
                }))
                .doOnSuccess(user -> logger.info("{} user check/creation complete. User ID: {}", roleName, user.getUserId()))
                .then()
                .doOnError(error -> logger.error("Error during {} initialization.", roleName, error))
                .doOnSuccess(v -> logger.info("{} initialization process finished successfully.", roleName))
                .block();
    }
}
