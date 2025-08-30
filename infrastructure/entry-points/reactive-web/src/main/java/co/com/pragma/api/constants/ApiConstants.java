package co.com.pragma.api.constants;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ApiConstants {

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static final class ApiParams {
        public static final String ID_NUMBER_PARAM = "idNumber";
    }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static final class ApiPaths {
        public static final String BASE_PATH = "/api/v1";
        public static final String USERS_PATH = BASE_PATH + "/usuarios";
        public static final String USER_BY_ID_NUMBER_PATH = USERS_PATH + "/{"+ApiParams.ID_NUMBER_PARAM+"}";
    }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static final class Operations {
        public static final String SAVE_USER_OPERATION_ID = "saveUser";
        public static final String SAVE_USER_REQUEST_BODY_DESC = "User Requested Data";
        public static final String FIND_USER_BY_ID_NUMBER_OPERATION_ID = "findUserByIdNumber";
    }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static final class Responses {
        public static final String SAVE_USER_SUCCESS_CREATED_DESC = "User Created Successfully";
        public static final String SUCCESS_CREATED_CODE = "201";
        public static final String SAVE_USER_BAD_REQUEST_DESC = "Invalid request (e.g. missing or incorrectly formatted data)";
        public static final String BAD_REQUEST_CODE = "400";
        public static final String SAVE_USER_CONFLICT_DESC = "Data conflict (e.g. email already exists)";
        public static final String CONFLICT_CODE = "409";
        public static final String FIND_USER_SUCCESS_DESC = "User Found Successfully";
        public static final String SUCCESS_OK_CODE = "200";
        public static final String FIND_USER_BY_ID_NUMBER_NOT_FOUND_DESC = "User with provided ID not found";
        public static final String NOT_FOUND_CODE = "404";
    }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static final class User {
        public static final String DESCRIPTION_USER_ID = "User's unique identifier.";
        public static final String EXAMPLE_USER_ID = "123";

        public static final String DESCRIPTION_NAME = "User's first name";
        public static final String EXAMPLE_NAME = "John";

        public static final String DESCRIPTION_LAST_NAME = "User's last name";
        public static final String EXAMPLE_LAST_NAME = "Doe";

        public static final String DESCRIPTION_EMAIL = "User's unique email address";
        public static final String EXAMPLE_EMAIL = "john.doe@example.com";

        public static final String DESCRIPTION_ID_NUMBER = "User's identification number";
        public static final String EXAMPLE_ID_NUMBER = "123456789";

        public static final String DESCRIPTION_BASE_SALARY = "User's base salary";
        public static final String EXAMPLE_BASE_SALARY = "5000000";

        public static final String DESCRIPTION_PHONE = "User's contact phone number";
        public static final String EXAMPLE_PHONE = "3001234567";

        public static final String DESCRIPTION_ADDRESS = "User's home address";
        public static final String EXAMPLE_ADDRESS = "Main St 123, Anytown";

        public static final String DESCRIPTION_BIRTH_DATE = "User's date of birth";
        public static final String EXAMPLE_BIRTH_DATE = "1990-01-15";
    }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static final class Role {
        public static final String DESCRIPTION_ROLE = "User's Role";
        public static final String DESCRIPTION_ROLE_ID = "Role's identifier";
        public static final String EXAMPLE_ROLE_ID = "3";

        public static final String DESCRIPTION_ROLE_NAME = "Role's name";
        public static final String EXAMPLE_ROLE_NAME = "CLIENTE";

        public static final String DESCRIPTION_ROLE_DESCRIPTION = "Role's description";
        public static final String EXAMPLE_ROLE_DESCRIPTION = "Cliente Solicitante";

    }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static final class Error {
        public static final String DESCRIPTION_TIMESTAMP = "Timestamp of when the error occurred.";
        public static final String DESCRIPTION_PATH = "The path of the API endpoint that was called.";
        public static final String DESCRIPTION_CODE = "A unique code identifying the error.";
        public static final String DESCRIPTION_MESSAGE = "A human-readable message describing the error.";
    }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static final class ValidationMessages {
        public static final String NAME_NOT_BLANK = "Name can't be empty";
        public static final String NAME_SIZE = "Name must have less than 50 characters";
        public static final String LAST_NAME_NOT_BLANK = "Last name can't be empty";
        public static final String LAST_NAME_SIZE = "Last name must have less than 50 characters";
        public static final String EMAIL_NOT_BLANK = "Email can't be empty";
        public static final String ID_NUMBER_NOT_BLANK = "Id number can't be empty";
        public static final String EMAIL_VALID = "Email should be valid";
        public static final String EMAIL_SIZE = "Email must have less than 100 characters";
        public static final String ID_NUMBER_SIZE = "Id Number must have less than 50 characters";
        public static final String SALARY_NOT_NULL = "Base Salary can't be empty";
        public static final String SALARY_MIN = "Base Salary must be at least 1";
        public static final String SALARY_MAX = "Base Salary must be less than 15,000,000";
        public static final String PHONE_SIZE = "Phone must have less than 20 characters";
        public static final String ADDRESS_SIZE = "Address must have less than 255 characters";
    }
}