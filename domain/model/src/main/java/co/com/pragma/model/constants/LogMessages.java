package co.com.pragma.model.constants;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class LogMessages {
    public static final String START_SAVING_USER_PROCESS = "Start saving user process";
    public static final String SAVED_USER = "Saved user";
    public static final String FINDING_USER_BY_ID_NUMBER = "Finding user by ID number: {}";
    public static final String USER_WITH_ID_NUMBER_FOUND = "User with ID number found: {}";
    public static final String ERROR_FINDING_USER_BY_ID_NUMBER = "Error finding user by ID number: {}";
    public static final String ERROR_FINDING_USER_BY_EMAIL = "Error finding user by email: {}";
    public static final String PASSWORD_MISMATCH = "Authentication failed due to password mismatch for email: {}";
    public static final String USER_NOT_FOUND_FOR_AUTH = "Authentication failed because user was not found for email: {}";
    public static final String GLOBAL_EXCEPTION_HANDLER_ERROR = "An error occurred for request [{}]:";
}
