package co.com.pragma.usecase.user;

import co.com.pragma.model.logs.gateways.LoggerPort;
import co.com.pragma.model.transaction.gateways.TransactionalPort;
import co.com.pragma.model.user.Role;
import co.com.pragma.model.user.User;
import co.com.pragma.model.user.constants.DefaultValues;
import co.com.pragma.model.user.constants.ErrorMessage;
import co.com.pragma.model.user.constants.LogMessages;
import co.com.pragma.model.user.exceptions.*;
import co.com.pragma.model.user.gateways.RoleRepository;
import co.com.pragma.model.user.gateways.UserRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class UserUseCase {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final LoggerPort logger;
    private final TransactionalPort transactionalPort;

    public Mono<User> saveUser(User user) {
        UserException validationError = verifyUserData(user);
        if (validationError != null) {
            logger.error(validationError.getMessage(), validationError);
            return Mono.error(validationError);
        }

        user.trimFields();
        if (user.getRole() == null || user.getRole().getName() == null) {
            user.setRole(Role.builder().name(DefaultValues.DEFAULT_ROLE).build());
        }

        return saveUserPersistenceOperation(user)
                .doOnSuccess(savedUser -> logger.info(LogMessages.SAVED_USER + " {}", savedUser))
                .as(transactionalPort::transactional);
    }

    // START Private methods ***********************************************************

    private Mono<User> saveUserPersistenceOperation(User user) {
        logger.info(LogMessages.START_SAVING_USER_PROCESS + " {}", user);
        return roleRepository.findOne(user.getRole())
                .switchIfEmpty(Mono.defer(() -> {
                    logger.error(ErrorMessage.ROL_NOT_FOUND, new RoleNotFoundException());
                    return Mono.error(new RoleNotFoundException());
                }))
                .flatMap(rol -> userRepository.exists(User.builder().email(user.getEmail()).build())
                        .flatMap(exists -> {
                            if (Boolean.TRUE.equals(exists)) {
                                logger.error(ErrorMessage.EMAIL_TAKEN, new EmailTakenException());
                                return Mono.error(new EmailTakenException());
                            }
                            logger.info(LogMessages.INSERT_USER_DB + " {}", user);
                            return userRepository.save(user.toBuilder().role(rol).build());
                        })
                );
    }

    private UserException verifyUserData(User user) {
        if (user == null || user.getName() == null || user.getName().isBlank() ||
                user.getLastName() == null || user.getLastName().isBlank() ||
                user.getEmail() == null || user.getEmail().isBlank() ||
                user.getBaseSalary() == null) {
            return new UserFieldException();
        }
        if (user.getName().length() > DefaultValues.MAX_LENGTH_NAME ||
                user.getLastName().length() > DefaultValues.MAX_LENGTH_LAST_NAME ||
                user.getEmail().length() > DefaultValues.MAX_LENGTH_EMAIL ||
                (user.getIdNumber() != null && user.getIdNumber().length() > DefaultValues.MAX_LENGTH_ID_NUMBER) ||
                (user.getPhone() != null && user.getPhone().length() > DefaultValues.MAX_LENGTH_PHONE) ||
                (user.getAddress() != null && user.getAddress().length() > DefaultValues.MAX_LENGTH_ADDRESS)
        ) {
            return new SizeOutOfBoundsException();
        }
        if (user.getBaseSalary().compareTo(DefaultValues.MIN_SALARY) < 0 ||
                user.getBaseSalary().compareTo(DefaultValues.MAX_SALARY) > 0) {
            return new SalaryUnboundException();
        }
        if (!user.getEmail().matches(DefaultValues.EMAIL_REGEX)) {
            return new EmailFormatException();
        }
        return null;
    }
    // END Private methods ***********************************************************

}
