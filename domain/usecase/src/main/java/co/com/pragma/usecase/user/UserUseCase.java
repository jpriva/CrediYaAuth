package co.com.pragma.usecase.user;

import co.com.pragma.model.constants.DefaultValues;
import co.com.pragma.model.constants.ErrorMessage;
import co.com.pragma.model.constants.LogMessages;
import co.com.pragma.model.exceptions.EmailTakenException;
import co.com.pragma.model.exceptions.IdNumberTakenException;
import co.com.pragma.model.exceptions.RoleNotFoundException;
import co.com.pragma.model.exceptions.UserNullException;
import co.com.pragma.model.logs.gateways.LoggerPort;
import co.com.pragma.model.role.gateways.RoleRepository;
import co.com.pragma.model.transaction.gateways.TransactionalPort;
import co.com.pragma.model.user.User;
import co.com.pragma.model.user.gateways.UserRepository;
import co.com.pragma.usecase.user.utils.UserUtils;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

/**
 * Use case for user management.
 * Contains the main business logic for user operations.
 */
@RequiredArgsConstructor
public class UserUseCase {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final LoggerPort logger;
    private final TransactionalPort transactionalPort;

    public Mono<User> saveUser(User user) {
        return Mono.justOrEmpty(user)
                .switchIfEmpty(Mono.defer(() -> Mono.error(new UserNullException())))
                .flatMap(UserUtils::trim)
                .flatMap(UserUtils::verifyUserData)
                .map(UserUtils::assignDefaultRollIfMissing)
                .flatMap(this::saveUserTransaction)
                .doFirst(() -> logger.info(LogMessages.START_SAVING_USER_PROCESS + " for email: {}", user != null ? user.getEmail() : "null"))
                .doOnError(ex -> logger.error(ErrorMessage.ERROR_SAVING_USER + " for email: {}", (user != null ? user.getEmail() : "null"), ex))
                .doOnSuccess(savedUser -> logger.info(LogMessages.SAVED_USER + " with ID: {}", savedUser.getUserId()))
                .as(transactionalPort::transactional);
    }

    // START Private methods ***********************************************************

    private Mono<User> saveUserTransaction(User user) {
        return findAndValidateRole(user)
                .flatMap(userWithRole ->
                        Mono.when(
                                    checkEmail(userWithRole),
                                    checkIdNumber(userWithRole)
                                )
                                .thenReturn(userWithRole)
                )
                .flatMap(userRepository::save);
    }

    private Mono<User> findAndValidateRole(User user) {
        return roleRepository.findOne(user.getRole())
                .switchIfEmpty(Mono.defer(() -> Mono.error(new RoleNotFoundException())))
                .map(role -> user.toBuilder().role(role).build());
    }

    private Mono<Void> checkEmail(User user) {
        return userRepository.exists(User.builder().email(user.getEmail()).build())
                .filter(exists -> !exists)
                .switchIfEmpty(Mono.defer(() -> Mono.error(new EmailTakenException())))
                .then();
    }

    private Mono<Void> checkIdNumber(User user) {
        if (!DefaultValues.ID_NUMBER_UNIQUE) {
            return Mono.empty();
        }
        return userRepository.exists(User.builder().idNumber(user.getIdNumber()).build())
                .filter(exists -> !exists)
                .switchIfEmpty(Mono.defer(() -> Mono.error(new IdNumberTakenException())))
                .then();
    }

    // END Private methods ***********************************************************

}
