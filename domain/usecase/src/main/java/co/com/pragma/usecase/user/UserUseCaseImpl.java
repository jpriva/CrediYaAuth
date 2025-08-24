package co.com.pragma.usecase.user;

import co.com.pragma.model.user.User;
import co.com.pragma.model.user.exceptions.*;
import co.com.pragma.model.user.gateways.UserRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

@RequiredArgsConstructor
public class UserUseCaseImpl implements UserUseCase {

    private final UserRepository userRepository;

    public Mono<User> save(User user){
        if (user == null || user.getName() == null || user.getName().isBlank() ||
                user.getLastName() == null || user.getLastName().isBlank() ||
                user.getEmail() == null || user.getEmail().isBlank() ||
                user.getBaseSalary() == null) {
            return Mono.error(new UserFieldException());
        }
        if (user.getBaseSalary().compareTo(BigDecimal.ZERO) < 0 ||
                user.getBaseSalary().compareTo(new BigDecimal(15000000)) > 0) {
            return Mono.error(new SalaryUnboundException());
        }
        if (!user.getEmail().matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
            return Mono.error(new EmailFormatException());
        }
        user.trimFields();
        return userRepository.exists(User.builder().email(user.getEmail()).build())
                .flatMap(exists -> Boolean.TRUE.equals(exists)
                        ? Mono.error(new EmailTakenException())
                        : userRepository.save(user));
    }

}
