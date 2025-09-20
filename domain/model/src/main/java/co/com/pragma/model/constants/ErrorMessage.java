package co.com.pragma.model.constants;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ErrorMessage {

    public static final String FILTER_IS_EMPTY_CODE = "F001";
    public static final String FILTER_IS_EMPTY = "Filter is empty.";

    public static final String USER_NOT_FOUND_CODE = "U001";
    public static final String USER_NOT_FOUND = "User not found.";

    public static final String REQUIRED_FIELDS_CODE = "U002";
    public static final String REQUIRED_FIELDS = "is a required field and must be provided.";

    public static final String SALARY_UNBOUND_CODE = "U003";
    public static final String SALARY_UNBOUND = "Base salary must be between 0 and $15,000,000.";

    public static final String EMAIL_FORMAT_CODE = "U004";
    public static final String EMAIL_FORMAT = "Invalid email format.";

    public static final String EMAIL_TAKEN_CODE = "U005";
    public static final String EMAIL_TAKEN = "Email is already taken.";

    public static final String ID_NUMBER_TAKEN_CODE = "U006";
    public static final String ID_NUMBER_TAKEN = "Id number is already taken.";

    public static final String SIZE_OUT_OF_BOUNDS_CODE = "U007";
    public static final String SIZE_OUT_OF_BOUNDS = "Size out of bounds.";

    public static final String USER_NULL_CODE = "U008";
    public static final String USER_NULL = "User is null.";

    public static final String ERROR_SAVING_USER_CODE = "U009";
    public static final String ERROR_SAVING_USER = "Error saving user.";

    public static final String USER_FIELD_ERROR_CODE = "U010";
    public static final String USER_FIELD_ERROR = "Error in user field.";

    public static final String ROL_NOT_FOUND_CODE = "R001";
    public static final String ROL_NOT_FOUND = "Rol not found.";

    public static final String FAIL_READ_REQUEST_CODE = "W001";
    public static final String FAIL_READ_REQUEST = "Failed to read HTTP message.";

    public static final String JSON_DECODING_ERROR_CODE = "J001";
    public static final String JSON_DECODING_ERROR = "Error decoding JSON.";

    public static final String UNKNOWN_CODE = "UNKNOWN";
    public static final String UNKNOWN_ERROR = "We are sorry, something went wrong. Please try again later.";

    public static final String INVALID_CREDENTIALS_CODE = "IC001";
    public static final String INVALID_CREDENTIALS = "Invalid credentials.";

    public static final String INVALID_ENDPOINT_CODE = "IE001";
    public static final String INVALID_ENDPOINT = "Invalid endpoint.";

    public static final String ACCESS_DENIED_CODE = "AD001";
    public static final String ACCESS_DENIED = "Access denied. You do not have the necessary permissions to access this resource.";
}
