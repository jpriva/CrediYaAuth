package co.com.pragma.usecase.user;

import co.com.pragma.model.user.Role;
import co.com.pragma.model.user.User;
import co.com.pragma.model.user.exceptions.*;
import co.com.pragma.model.user.gateways.RoleRepository;
import co.com.pragma.model.user.gateways.UserRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

@RequiredArgsConstructor
public class UserUseCaseImpl implements UserUseCase {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    public Mono<User> save(User user) {
        UserException validationError = verifyUserData(user);
        if (validationError != null) {
            return Mono.error(validationError);
        }

        user.trimFields();
        if (user.getRole() == null || user.getRole().getName() == null) {
            user.setRole(Role.builder().name("CLIENTE").build());
        }

        return roleRepository.findOne(user.getRole())
                .switchIfEmpty(Mono.error(new RoleNotFoundException()))
                .flatMap(rol -> userRepository.exists(User.builder().email(user.getEmail()).build())
                        .flatMap(exists -> exists
                                ? Mono.error(new EmailTakenException())
                                : userRepository.save(user.toBuilder().role(rol).build())
                        )
                );
    }

    private UserException verifyUserData(User user){
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
