package co.com.pragma.usecase.auth;

import co.com.pragma.model.constants.LogMessages;
import co.com.pragma.model.exceptions.FieldBlankException;
import co.com.pragma.model.exceptions.InvalidCredentialsException;
import co.com.pragma.model.exceptions.RoleNotFoundException;
import co.com.pragma.model.logs.gateways.LoggerPort;
import co.com.pragma.model.password.gateways.PasswordEncoderPort;
import co.com.pragma.model.role.gateways.RoleRepository;
import co.com.pragma.model.user.User;
import co.com.pragma.model.user.gateways.UserRepository;
import co.com.pragma.usecase.utils.ValidationUtils;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import static co.com.pragma.model.constants.DefaultValues.EMAIL_FIELD;
import static co.com.pragma.model.constants.DefaultValues.PASSWORD_FIELD;

@RequiredArgsConstructor
public class AuthUseCase {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoderPort passwordEncoderPort;
    private final LoggerPort logger;

    public Mono<User> authenticate(String email, String rawPassword) {
        Mono<Void> inputValidation = Mono.when(
                ValidationUtils.validateCondition(rawPassword != null && !rawPassword.isBlank(),
                        () -> new FieldBlankException(PASSWORD_FIELD)),
                ValidationUtils.validateCondition(email != null && !email.isBlank(),
                        () -> new FieldBlankException(EMAIL_FIELD))
        );

        return inputValidation
                .then(Mono.defer(() -> userRepository.findWithPasswordByEmail(email)))
                .doOnError(ex -> logger.error(LogMessages.ERROR_FINDING_USER_BY_EMAIL, email, ex))
                .flatMap(user -> {
                    if (passwordEncoderPort.matches(rawPassword, user.getPassword()) && user.getRole() != null && user.getRole().getRolId() != null) {
                        return roleRepository.findById(user.getRole().getRolId())
                                .map(role -> user.toBuilder().role(role).password(null).build());
                    } else if (user.getRole() == null || user.getRole().getRolId() == null) {
                        logger.error(LogMessages.ERROR_FINDING_ROLE);
                        return Mono.error(new RoleNotFoundException());
                    }else {
                        logger.warn(LogMessages.PASSWORD_MISMATCH, email);
                        return Mono.error(new InvalidCredentialsException());
                    }
                })
                .switchIfEmpty(Mono.defer(() -> {
                    logger.warn(LogMessages.USER_NOT_FOUND_FOR_AUTH, email);
                    return Mono.error(new InvalidCredentialsException());
                }));
    }
}