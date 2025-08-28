package co.com.pragma.usecase.user.utils;

import co.com.pragma.model.user.User;
import co.com.pragma.usecase.user.constants.DefaultValues;
import co.com.pragma.usecase.user.exceptions.*;

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
    public static CustomException verifyUserData(User user) {
        CustomException exception = verifyUserBlankFields(user);
        if (exception != null) return exception;

        exception = verifyUserFieldsBounds(user);
        if (exception != null) return exception;

        if (!user.getEmail().matches(DefaultValues.EMAIL_REGEX)) {
            return new EmailFormatException();
        }
        return null;
    }

    /**
     * Verifies that all mandatory fields in the user object are not null or blank.
     *
     * @param user The user to validate.
     * @return A {@link FieldBlankException} if a mandatory field is invalid, otherwise null.
     */
    private static CustomException verifyUserBlankFields(User user){
        if (user.getName() == null || user.getName().isBlank()) {
            return new FieldBlankException(DefaultValues.NAME_FIELD);
        }
        if (user.getLastName() == null || user.getLastName().isBlank()) {

            return new FieldBlankException(DefaultValues.LAST_NAME_FIELD);
        }
        if (user.getEmail() == null || user.getEmail().isBlank()){
            return new FieldBlankException(DefaultValues.EMAIL_FIELD);
        }
        if (user.getBaseSalary() == null) {
            return new FieldBlankException(DefaultValues.SALARY_FIELD);
        }
        return null;
    }

    /**
     * Verifies that the user's fields do not exceed their defined size or value bounds.
     *
     * @param user The user to validate.
     * @return A {@link FieldSizeOutOfBoundsException} or {@link SalaryUnboundException} if a field is invalid, otherwise null.
     */
    private static CustomException verifyUserFieldsBounds(User user){
        if (user.getName().length() > DefaultValues.MAX_LENGTH_NAME){
            return new FieldSizeOutOfBoundsException(DefaultValues.NAME_FIELD);
        }
        if (user.getLastName().length() > DefaultValues.MAX_LENGTH_LAST_NAME){
            return new FieldSizeOutOfBoundsException(DefaultValues.LAST_NAME_FIELD);
        }
        if (user.getEmail().length() > DefaultValues.MAX_LENGTH_EMAIL){
            return new FieldSizeOutOfBoundsException(DefaultValues.EMAIL_FIELD);
        }
        if (user.getIdNumber() != null &&
                user.getIdNumber().length() > DefaultValues.MAX_LENGTH_ID_NUMBER) {
            return new FieldSizeOutOfBoundsException(DefaultValues.ID_NUMBER_FIELD);
        }
        if (user.getPhone() != null &&
                user.getPhone().length() > DefaultValues.MAX_LENGTH_PHONE) {
            return new FieldSizeOutOfBoundsException(DefaultValues.PHONE_FIELD);
        }
        if (user.getAddress() != null &&
                user.getAddress().length() > DefaultValues.MAX_LENGTH_ADDRESS){
            return new FieldSizeOutOfBoundsException(DefaultValues.ADDRESS_FIELD);
        }
        if (user.getBaseSalary().compareTo(DefaultValues.MIN_SALARY) < 0 ||
                user.getBaseSalary().compareTo(DefaultValues.MAX_SALARY) > 0) {
            return new SalaryUnboundException();
        }
        return null;
    }
}
