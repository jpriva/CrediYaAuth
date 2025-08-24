package co.com.pragma.usecase.user;

import co.com.pragma.model.logs.gateways.LoggerPort;
import co.com.pragma.model.user.Role;
import co.com.pragma.model.user.User;
import co.com.pragma.model.user.exceptions.*;
import co.com.pragma.model.transaction.gateways.TransactionalPort;
import co.com.pragma.model.user.gateways.RoleRepository;
import co.com.pragma.model.user.gateways.UserRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

@RequiredArgsConstructor
public class UserUseCase implements IUserUseCase {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final LoggerPort logger;
    private final TransactionalPort transactionalPort;

    public Mono<User> save(User user) {
        UserException validationError = verifyUserData(user);
        if (validationError != null) {
            logger.error("Validation error saving user", validationError);
            return Mono.error(validationError);
        }

        logger.info("Starting save process for user: {}", user);

        user.trimFields();
        if (user.getRole() == null || user.getRole().getName() == null) {
            user.setRole(Role.builder().name("CLIENTE").build());
        }

        return roleRepository.findOne(user.getRole())
                .switchIfEmpty(Mono.defer(() -> {
                    logger.error("Role not found", new RoleNotFoundException());
                    return Mono.error(new RoleNotFoundException());
                }))
                .flatMap(rol -> userRepository.exists(User.builder().email(user.getEmail()).build())
                        .flatMap(exists -> {
                            if (Boolean.TRUE.equals(exists)){
                                logger.error("Email already taken", new EmailTakenException());
                                return Mono.error(new EmailTakenException());
                            }
                            logger.info("Saving user {}", user);
                            return userRepository.save(user.toBuilder().role(rol).build());
                        })
                )
                .as(transactionalPort::transactional);
    }

    private UserException verifyUserData(User user) {
        if (user == null || user.getName() == null || user.getName().isBlank() ||
                user.getLastName() == null || user.getLastName().isBlank() ||
                user.getEmail() == null || user.getEmail().isBlank() ||
                user.getBaseSalary() == null) {
            return new UserFieldException();
        }
        if (user.getBaseSalary().compareTo(BigDecimal.ZERO) < 0 ||
                user.getBaseSalary().compareTo(new BigDecimal(15000000)) > 0) {
            return new SalaryUnboundException();
        }
        if (!user.getEmail().matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
            return new EmailFormatException();
        }
        return null;
    }

}
