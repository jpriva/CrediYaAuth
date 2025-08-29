package co.com.pragma.usecase.user;

import co.com.pragma.model.constants.ErrorMessage;
import co.com.pragma.model.constants.LogMessages;
import co.com.pragma.model.exceptions.*;
import co.com.pragma.model.logs.gateways.LoggerPort;
import co.com.pragma.model.role.Role;
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

    /**
     * Saves a new user in the system after performing business validations.
     * <p>
     * This method orchestrates the entire user creation process. It first validates the input,
     * assigns a default role if none is provided, and then executes the database operations
     * within a single transaction to ensure data integrity.
     *
     * @param user The {@link User} object to be saved. It must not be null.
     * @return A {@link Mono} that emits the saved {@link User} with its new ID upon success.
     * If any validation or persistence error occurs, the Mono will emit an error.
     * @throws UserNullException             if the provided user object is null.
     * @throws FieldBlankException           if a required field (like name, lastName, email, or baseSalary) is null or blank.
     * @throws FieldSizeOutOfBoundsException if a field's length exceeds its defined maximum.
     * @throws SalaryUnboundException        if the baseSalary is outside the allowed range.
     * @throws EmailFormatException          if the email does not match the required format.
     * @throws RoleNotFoundException         if the specified role does not exist in the system.
     * @throws EmailTakenException           if the provided email is already in use by another user.
     */
    public Mono<User> saveUser(User user) {
        return Mono.justOrEmpty(user)
                .switchIfEmpty(Mono.error(new UserNullException()))
                .flatMap(UserUtils::trim)
                .flatMap(UserUtils::verifyUserData)
                .map(UserUtils::assignDefaultRollIfMissing)
                .flatMap(this::saveUserTransaction)
                .doFirst(() -> logger.info(LogMessages.START_SAVING_USER_PROCESS + " for email: {}", user != null ? user.getEmail() : "null"))
                .doOnError(ex -> logger.error(ErrorMessage.ERROR_SAVING_USER + " for email: {}", user.getEmail(), ex))
                .doOnSuccess(savedUser -> logger.info(LogMessages.SAVED_USER + " with ID: {}", savedUser.getUserId()))
                .as(transactionalPort::transactional);
    }

    // START Private methods ***********************************************************

    /**
     * Orchestrates the sequence of database operations that must be transactional.
     * This includes finding the role, checking for email existence, and saving the user.
     *
     * @param user The user to be processed.
     * @return A {@link Mono} that emits the saved user if all operations succeed.
     */
    private Mono<User> saveUserTransaction(User user) {
        return findAndValidateRole(user)
                .flatMap(role -> checkEmail(user, role))
                .flatMap(userRepository::save);
    }

    /**
     * Finds the user's role in the repository.
     * If the role is not found, it emits a {@link RoleNotFoundException}.
     *
     * @param user The user whose role is to be found.
     * @return A {@link Mono} that emits the found {@link Role}.
     */
    private Mono<Role> findAndValidateRole(User user) {
        return roleRepository.findOne(user.getRole())
                .switchIfEmpty(Mono.defer(() -> Mono.error(new RoleNotFoundException())));
    }

    /**
     * Checks if the user's email already exists.
     * If it exists, it emits an {@link EmailTakenException}.
     * If it does not exist, it prepares the user object with the validated role for saving.
     *
     * @param user The user object being processed.
     * @param role The validated role to be associated with the user.
     * @return A {@link Mono} containing the user ready to be saved.
     */
    private Mono<User> checkEmail(User user, Role role) {
        return userRepository.exists(User.builder().email(user.getEmail()).build())
                .filter(exists -> !exists)
                .switchIfEmpty(Mono.defer(() -> Mono.error(new EmailTakenException())))
                .flatMap(exists -> Mono.just(user.toBuilder().role(role).build()));
    }

    // END Private methods ***********************************************************

}
