package co.com.pragma.usecase.user.utils;

import co.com.pragma.model.exceptions.*;
import co.com.pragma.model.role.Role;
import co.com.pragma.model.user.User;
import co.com.pragma.model.constants.DefaultValues;
import co.com.pragma.usecase.utils.ValidationUtils;
import reactor.core.publisher.Mono;

import java.math.RoundingMode;

public class UserUtils {

    private UserUtils(){}

    /**
     * Performs a series of synchronous validations on the user object.
     * It aggregates checks for blank fields, field bounds, and email format.
     *
     * @param user The user to validate.
     * @return A {@link CustomException} instance if a validation error is found, or null if the data is valid.
     */
    public static Mono<User> verifyUserData(User user) {
        return verifyUserBlankFields(user)
                .then(verifyUserFieldsBounds(user))
                .then(verifyEmailFormat(user))
                .thenReturn(user);
    }

    /**
     * Verifies that all mandatory fields in the user object are not null or blank.
     *
     * @param user The user to validate.
     * @return A {@link FieldBlankException} if a mandatory field is invalid, otherwise null.
     */
    private static Mono<Void> verifyUserBlankFields(User user) {
        return ValidationUtils.validate(user.getName() != null && !user.getName().isBlank(), () -> new FieldBlankException(DefaultValues.NAME_FIELD))
                .then(ValidationUtils.validate(user.getLastName() != null && !user.getLastName().isBlank(), () -> new FieldBlankException(DefaultValues.LAST_NAME_FIELD)))
                .then(ValidationUtils.validate(user.getEmail() != null && !user.getEmail().isBlank(), () -> new FieldBlankException(DefaultValues.EMAIL_FIELD)))
                .then(ValidationUtils.validate(user.getBaseSalary() != null, () -> new FieldBlankException(DefaultValues.SALARY_FIELD)));
    }

    /**
     * Verifies that the user's fields do not exceed their defined size or value bounds.
     *
     * @param user The user to validate.
     * @return A {@link FieldSizeOutOfBoundsException} or {@link SalaryUnboundException} if a field is invalid, otherwise null.
     */
    private static Mono<Void> verifyUserFieldsBounds(User user) {
        return ValidationUtils.validate(user.getName().length() <= DefaultValues.MAX_LENGTH_NAME, () -> new FieldSizeOutOfBoundsException(DefaultValues.NAME_FIELD))
                .then(ValidationUtils.validate(user.getLastName().length() <= DefaultValues.MAX_LENGTH_LAST_NAME, () -> new FieldSizeOutOfBoundsException(DefaultValues.LAST_NAME_FIELD)))
                .then(ValidationUtils.validate(user.getEmail().length() <= DefaultValues.MAX_LENGTH_EMAIL, () -> new FieldSizeOutOfBoundsException(DefaultValues.EMAIL_FIELD)))
                .then(ValidationUtils.validate(user.getIdNumber() == null || user.getIdNumber().length() <= DefaultValues.MAX_LENGTH_ID_NUMBER, () -> new FieldSizeOutOfBoundsException(DefaultValues.ID_NUMBER_FIELD)))
                .then(ValidationUtils.validate(user.getPhone() == null || user.getPhone().length() <= DefaultValues.MAX_LENGTH_PHONE, () -> new FieldSizeOutOfBoundsException(DefaultValues.PHONE_FIELD)))
                .then(ValidationUtils.validate(user.getAddress() == null || user.getAddress().length() <= DefaultValues.MAX_LENGTH_ADDRESS, () -> new FieldSizeOutOfBoundsException(DefaultValues.ADDRESS_FIELD)))
                .then(ValidationUtils.validate(user.getBaseSalary().compareTo(DefaultValues.MIN_SALARY) >= 0, SalaryUnboundException::new))
                .then(ValidationUtils.validate(user.getBaseSalary().compareTo(DefaultValues.MAX_SALARY) <= 0, SalaryUnboundException::new));
    }

    private static Mono<Void> verifyEmailFormat(User user) {
        return ValidationUtils.validate(user.getEmail().matches(DefaultValues.EMAIL_REGEX), EmailFormatException::new);
    }

    /**
     * Trims string fields and scales the salary of a User object, returning a new, immutable instance.
     * This operation is wrapped in a Mono to be used in a reactive chain.
     *
     * @param user The user to process.
     * @return A {@link Mono} emitting a new {@link User} instance with trimmed and scaled fields.
     */
    public static Mono<User> trim(User user) {
        return Mono.fromCallable(() -> user.toBuilder()
                .name(user.getName() != null ? user.getName().trim() : null)
                .lastName(user.getLastName() != null ? user.getLastName().trim() : null)
                .email(user.getEmail() != null ? user.getEmail().trim() : null)
                .idNumber(user.getIdNumber() != null ? user.getIdNumber().trim() : null)
                .phone(user.getPhone() != null ? user.getPhone().trim() : null)
                .address(user.getAddress() != null ? user.getAddress().trim() : null)
                .baseSalary(user.getBaseSalary() != null ? user.getBaseSalary().setScale(2, RoundingMode.HALF_UP) : null)
                .build());
    }

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

}
