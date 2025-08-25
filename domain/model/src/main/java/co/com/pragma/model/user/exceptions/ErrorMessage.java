package co.com.pragma.model.user.exceptions;

public final class ErrorMessage {
    public static final String USER_NOT_FOUND = "User not found.";
    public static final String ROL_NOT_FOUND = "Rol not found.";
    public static final String REQUIRED_FIELDS = "All required fields must be provided.";
    public static final String SALARY_UNBOUND = "Base salary must be between 0 and $15,000,000.";
    public static final String EMAIL_FORMAT = "Invalid email format.";
    public static final String EMAIL_TAKEN = "Email is already taken.";
    public static final String SIZE_OUT_OF_BOUNDS = "Size out of bounds.";
    public static final String USER_NOT_FOUND_CODE = "U001";
    public static final String ROL_NOT_FOUND_CODE = "R001";
    public static final String REQUIRED_FIELDS_CODE = "U002";
    public static final String SALARY_UNBOUND_CODE = "U003";
    public static final String EMAIL_FORMAT_CODE = "U004";
    public static final String EMAIL_TAKEN_CODE = "U005";
    public static final String SIZE_OUT_OF_BOUNDS_CODE = "U006";

    private ErrorMessage() {}
}
