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
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import static co.com.pragma.model.constants.DefaultValues.EMAIL_FIELD;
import static co.com.pragma.model.constants.DefaultValues.PASSWORD_FIELD;
import static co.com.pragma.usecase.utils.ValidationUtils.validateCondition;

@RequiredArgsConstructor
public class AuthUseCase {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoderPort passwordEncoderPort;
    private final LoggerPort logger;

    public Mono<User> authenticate(String email, String rawPassword) {
        Mono<Void> inputValidation = Mono.when(
                validateCondition(rawPassword != null && !rawPassword.isBlank(),
                        () -> new FieldBlankException(PASSWORD_FIELD)),
                validateCondition(email != null && !email.isBlank(),
                        () -> new FieldBlankException(EMAIL_FIELD))
        );

        return inputValidation
                .then(Mono.defer(() -> userRepository.findWithPasswordByEmail(email)))
                .doOnError(ex -> logger.error(LogMessages.ERROR_FINDING_USER_BY_EMAIL, email, ex))
                .flatMap(user -> validateCondition(passwordEncoderPort.matches(rawPassword, user.getPassword()), InvalidCredentialsException::new)
                        .then(validateCondition(user.getRole() != null && user.getRole().getRolId() != null, RoleNotFoundException::new))
                        .then(Mono.defer(() -> roleRepository.findById(user.getRole().getRolId())))
                        .map(role -> user.toBuilder().role(role).password(null).build())
                )
                .switchIfEmpty(Mono.defer(() -> {
                    logger.warn(LogMessages.USER_NOT_FOUND_FOR_AUTH, email);
                    return Mono.error(new InvalidCredentialsException());
                }));
    }
}