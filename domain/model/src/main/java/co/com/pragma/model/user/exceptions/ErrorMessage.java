package co.com.pragma.model.user.exceptions;

public final class ErrorMessage {
    public static final String USER_NOT_FOUND = "User not found.";
    public static final String REQUIRED_FIELDS = "All required fields must be provided.";
    public static final String SALARY_UNBOUND = "Base salary must be between 0 and $15,000,000.";
    public static final String EMAIL_FORMAT = "Invalid email format.";
    public static final String EMAIL_TAKEN = "Email is already taken.";

    private ErrorMessage() {}
}
