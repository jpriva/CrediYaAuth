package co.com.pragma.usecase.user.utils;

import co.com.pragma.model.exceptions.*;
import co.com.pragma.model.role.Role;
import co.com.pragma.model.user.User;
import co.com.pragma.model.constants.DefaultValues;
import co.com.pragma.model.user.filters.UserFilter;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import co.com.pragma.usecase.utils.ValidationUtils;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.math.RoundingMode;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UserUtils {

    /**
     * Trims string fields and scales the salary of a User object, returning a new, immutable instance.
     * This operation is wrapped in a Mono to be used in a reactive chain.
     *
     * @param user The user to process.
     * @return A {@link Mono} emitting a new {@link User} instance with trimmed and scaled fields.
     */
    public static Mono<User> trim(User user) {
        return Mono.justOrEmpty(user)
                .map(u -> u.toBuilder()
                        .name(trimString(u.getName()))
                        .lastName(trimString(u.getLastName()))
                        .email(trimString(u.getEmail()))
                        .idNumber(trimString(u.getIdNumber()))
                        .phone(trimString(u.getPhone()))
                        .address(trimString(u.getAddress()))
                        .baseSalary(scaleBigDecimal(u.getBaseSalary()))
                        .password(trimString(u.getPassword()))
                        .build());
    }

    private static String trimString(String value) {
        return value != null ? value.trim() : null;
    }

    private static BigDecimal scaleBigDecimal(BigDecimal value) {
        return value != null ? value.setScale(2, RoundingMode.HALF_UP) : null;
    }

    /**
     * Performs a series of synchronous validations on the user object.
     * It aggregates checks for blank fields, field bounds, and email format.
     *
     * @param user The user to validate.
     * @return A {@link Mono} containing the validated user, or a {@link Mono#error(Throwable)} if any validation fails.
     */
    public static Mono<User> verifyUserData(User user) {
        return verifyUserBlankFields(user)
                .then(Mono.defer(() -> verifyUserFieldsBounds(user)))
                .then(Mono.defer(() -> verifyEmailFormat(user)))
                .thenReturn(user);
    }

    /**
     * Verifies that all mandatory fields in the user object are not null or blank.
     *
     * @param user The user to validate.
     * @return An empty {@link Mono} on successful validation, or a {@link Mono#error(Throwable)} on failure.
     */
    private static Mono<Void> verifyUserBlankFields(User user) {
        return ValidationUtils.validateCondition(user.getName() != null && !user.getName().isBlank(), () -> new FieldBlankException(DefaultValues.NAME_FIELD))
                .then(ValidationUtils.validateCondition(user.getLastName() != null && !user.getLastName().isBlank(), () -> new FieldBlankException(DefaultValues.LAST_NAME_FIELD)))
                .then(ValidationUtils.validateCondition(user.getEmail() != null && !user.getEmail().isBlank(), () -> new FieldBlankException(DefaultValues.EMAIL_FIELD)))
                .then(ValidationUtils.validateCondition(user.getIdNumber() != null && !user.getIdNumber().isBlank(), () -> new FieldBlankException(DefaultValues.ID_NUMBER_FIELD)))
                .then(ValidationUtils.validateCondition(user.getBaseSalary() != null, () -> new FieldBlankException(DefaultValues.SALARY_FIELD)))
                .then(ValidationUtils.validateCondition(user.getPassword() != null && !user.getPassword().isBlank(), () -> new FieldBlankException(DefaultValues.PASSWORD_FIELD)));
    }

    /**
     * Verifies that the user's fields do not exceed their defined size or value bounds.
     *
     * @param user The user to validate.
     * @return An empty {@link Mono} on successful validation, or a {@link Mono#error(Throwable)} on failure.
     */
    private static Mono<Void> verifyUserFieldsBounds(User user) {
        return ValidationUtils.validateCondition(user.getName().length() <= DefaultValues.MAX_LENGTH_NAME, () -> new FieldSizeOutOfBoundsException(DefaultValues.NAME_FIELD))
                .then(ValidationUtils.validateCondition(user.getLastName().length() <= DefaultValues.MAX_LENGTH_LAST_NAME, () -> new FieldSizeOutOfBoundsException(DefaultValues.LAST_NAME_FIELD)))
                .then(ValidationUtils.validateCondition(user.getEmail().length() <= DefaultValues.MAX_LENGTH_EMAIL, () -> new FieldSizeOutOfBoundsException(DefaultValues.EMAIL_FIELD)))
                .then(ValidationUtils.validateCondition(user.getIdNumber().length() <= DefaultValues.MAX_LENGTH_ID_NUMBER, () -> new FieldSizeOutOfBoundsException(DefaultValues.ID_NUMBER_FIELD)))
                .then(ValidationUtils.validateCondition(user.getPhone() == null || user.getPhone().length() <= DefaultValues.MAX_LENGTH_PHONE, () -> new FieldSizeOutOfBoundsException(DefaultValues.PHONE_FIELD)))
                .then(ValidationUtils.validateCondition(user.getAddress() == null || user.getAddress().length() <= DefaultValues.MAX_LENGTH_ADDRESS, () -> new FieldSizeOutOfBoundsException(DefaultValues.ADDRESS_FIELD)))
                .then(ValidationUtils.validateCondition(user.getBaseSalary().compareTo(DefaultValues.MIN_SALARY) >= 0, SalaryUnboundException::new))
                .then(ValidationUtils.validateCondition(user.getBaseSalary().compareTo(DefaultValues.MAX_SALARY) <= 0, SalaryUnboundException::new))
                .then(ValidationUtils.validateCondition(user.getPassword().length() >= DefaultValues.MIN_LENGTH_PASSWORD && user.getPassword().length() <= DefaultValues.MAX_LENGTH_PASSWORD, () -> new FieldSizeOutOfBoundsException(DefaultValues.PASSWORD_FIELD)));
    }

    /**
     * Verifies that the user's email matches the expected format.
     *
     * @param user The user to validate.
     * @return An empty {@link Mono} on successful validation, or a {@link Mono#error(Throwable)} on failure.
     */
    private static Mono<Void> verifyEmailFormat(User user) {
        return ValidationUtils.validateCondition(user.getEmail().matches(DefaultValues.EMAIL_REGEX), EmailFormatException::new);
    }

    /**
     * Assigns a default role to the user if no valid role reference is provided.
     * This method is immutable; it returns a new User instance if the role is added.
     *
     * @param user The user to check.
     * @return A new user instance with the default role if needed, or the original user instance.
     */
    public static User assignDefaultRollIfMissing(User user) {
        if (isRoleReferenceMissing(user.getRole())) {
            return user.toBuilder().role(Role.builder().name(DefaultValues.DEFAULT_ROLE_NAME).build()).build();
        }
        return user;
    }

    /**
     * Checks if a Role object is missing a valid reference (i.e., it has neither an ID nor a name).
     * @param role The Role object to check.
     * @return true if the role reference is missing, false otherwise.
     */
    private static boolean isRoleReferenceMissing(Role role) {
        return role == null || (role.getRolId() == null && (role.getName() == null || role.getName().isBlank()));
    }

    public static Mono<UserFilter> validateFilter(UserFilter filter) {
        return Mono.justOrEmpty(filter)
                .flatMap(f -> {
                    boolean hasAnyField = f.getSalaryGreaterThan() != null ||
                            f.getSalaryLowerThan() != null ||
                            f.getName() != null ||
                            f.getEmail() != null ||
                            f.getIdNumber() != null;
                    return hasAnyField ? Mono.just(f) : Mono.error(new FilterEmptyException());
                });
    }

}
