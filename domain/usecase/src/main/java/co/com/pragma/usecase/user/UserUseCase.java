package co.com.pragma.usecase.user;

import co.com.pragma.model.constants.DefaultValues;
import co.com.pragma.model.constants.ErrorMessage;
import co.com.pragma.model.constants.LogMessages;
import co.com.pragma.model.exceptions.EmailTakenException;
import co.com.pragma.model.exceptions.IdNumberTakenException;
import co.com.pragma.model.exceptions.RoleNotFoundException;
import co.com.pragma.model.exceptions.UserNullException;
import co.com.pragma.model.logs.gateways.LoggerPort;
import co.com.pragma.model.password.gateways.PasswordEncoderPort;
import co.com.pragma.model.queue.gateways.SQSPort;
import co.com.pragma.model.role.Role;
import co.com.pragma.model.role.gateways.RoleRepository;
import co.com.pragma.model.transaction.gateways.TransactionalPort;
import co.com.pragma.model.user.User;
import co.com.pragma.model.user.filters.UserFilter;
import co.com.pragma.model.user.gateways.UserRepository;
import co.com.pragma.usecase.user.utils.UserUtils;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

import static co.com.pragma.usecase.user.utils.UserUtils.validateFilter;

@RequiredArgsConstructor
public class UserUseCase {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final LoggerPort logger;
    private final PasswordEncoderPort passwordEncoderPort;
    private final TransactionalPort transactionalPort;
    private final SQSPort sqsPort;

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

    public Mono<User> findByIdNumber(String idNumber) {
        return userRepository.findOne(User.builder().idNumber(idNumber).build())
                .flatMap(UserUtils::trim)
                .flatMap(this::findAndValidateRole)
                .doFirst(() -> logger.info(LogMessages.FINDING_USER_BY_ID_NUMBER, idNumber))
                .doOnError(ex -> logger.error(LogMessages.ERROR_FINDING_USER_BY_ID_NUMBER, idNumber, ex))
                .doOnNext(user -> logger.info(LogMessages.USER_WITH_ID_NUMBER_FOUND, user.getUserId()));
    }

    public Flux<User> findUsersByEmails(List<String> emails) {
        return userRepository.findAllByEmail(emails)
                .doFirst(() -> logger.info(LogMessages.FINDING_USERS_BY_EMAILS, emails))
                .doOnError(ex -> logger.error(LogMessages.ERROR_FINDING_USERS_BY_EMAILS, emails, ex))
                .doOnNext(user -> logger.info(LogMessages.USER_WITH_EMAIL_FOUND, user.getEmail()));
    }

    public Flux<User> findUsersByFilter(UserFilter filter) {
        return validateFilter(filter)
                .flatMapMany(userRepository::findUsersByFilter)
                .doFirst(() -> logger.info(LogMessages.FINDING_USERS))
                .doOnError(ex -> logger.error(LogMessages.ERROR_FINDING_USERS, ex));
    }

    public Mono<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public Flux<String> findUserEmailsByFilter(UserFilter filter) {
        return validateFilter(filter)
                .flatMapMany(userRepository::findUserEmailsByFilter)
                .doFirst(() -> logger.info(LogMessages.FINDING_USER_EMAILS))
                .doOnError(ex -> logger.error(LogMessages.ERROR_FINDING_USER_EMAILS, ex));
    }

    public Mono<String> sendReportToAdmins(){
        return roleRepository.findOne(Role.builder().name(DefaultValues.ADMIN_ROLE_NAME).build())
                .flatMapMany(role->userRepository.findAllByRole(role.getRolId()))
                .map(User::getEmail)
                .collectList()
                .flatMap(sqsPort::sendEmails)
                .thenReturn("Sending emails");
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
                .map(userTrimmed -> userTrimmed.toBuilder().password(passwordEncoderPort.encode(userTrimmed.getPassword())).build())
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
