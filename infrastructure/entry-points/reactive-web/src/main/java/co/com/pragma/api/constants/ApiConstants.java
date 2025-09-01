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
        public static final String LOGIN_PATH = BASE_PATH + "/login";
        public static final String USER_BY_ID_NUMBER_PATH = USERS_PATH + "/{"+ApiParams.ID_NUMBER_PARAM+"}";
    }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static final class ApiPathMatchers {
        //PERMIT ALL
        public static final String LOGIN_MATCHER = ApiPaths.LOGIN_PATH + "/**";
        public static final String API_DOCS_MATCHER = "/v3/api-docs/**";
        public static final String SWAGGER_UI_MATCHER = "/swagger-ui/**";
        public static final String WEB_JARS_MATCHER = "/webjars/**";
        //ADMIN/ASESOR
        public static final String USER_MATCHER = ApiPaths.USERS_PATH + "/**";
        //TEST ENDPOINT
        public static final String TEST_MATCHER = "/test-endpoint";

    }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static final class Operations {
        public static final String SAVE_USER_OPERATION_ID = "saveUser";
        public static final String LOGIN_OPERATION_ID = "login";
        public static final String LOGIN_REQUEST_BODY_DESC = "User credentials for authentication";
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
        public static final String LOGIN_SUCCESS_DESC = "Authentication successful, JWT returned";
        public static final String LOGIN_BAD_REQUEST_DESC = "Invalid request (e.g. missing email or password)";
        public static final String CONFLICT_CODE = "409";
        public static final String FIND_USER_SUCCESS_DESC = "User Found Successfully";
        public static final String SUCCESS_OK_CODE = "200";
        public static final String FIND_USER_BY_ID_NUMBER_NOT_FOUND_DESC = "User with provided ID not found";
        public static final String NOT_FOUND_CODE = "404";
        public static final String UNAUTHORIZED_CODE = "401";
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

        public static final String DESCRIPTION_PASSWORD = "User's secure password";
        public static final String EXAMPLE_PASSWORD = "pass1234";

        public static final String DESCRIPTION_BIRTH_DATE = "User's date of birth";
        public static final String EXAMPLE_BIRTH_DATE = "1990-01-15";

        public static final String EXAMPLE_TOKEN = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9";
        public static final String DESCRIPTION_TOKEN = "Token for authentication";
    }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static final class Role {
        public static final String CLIENT_ROLE_NAME = "CLIENTE";
        public static final String ADMIN_ROLE_NAME = "ADMIN";
        public static final String ADVISOR_ROLE_NAME = "ASESOR";

        public static final String DESCRIPTION_ROLE = "User's Role";
        public static final String DESCRIPTION_ROLE_ID = "Role's identifier";
        public static final String EXAMPLE_ROLE_ID = "3";

        public static final String DESCRIPTION_ROLE_NAME = "Role's name";

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
        public static final String PASSWORD_NOT_NULL = "Password can't be empty";
        public static final String SALARY_MIN = "Base Salary must be at least 1";
        public static final String SALARY_MAX = "Base Salary must be less than 15,000,000";
        public static final String PHONE_SIZE = "Phone must have less than 20 characters";
        public static final String ADDRESS_SIZE = "Address must have less than 255 characters";
        public static final String PASSWORD_SIZE = "Password must have at least 8 characters";
    }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static final class ApiConfig {
        public static final String DESCRIPTION_BEARER_AUTH = "Enter the JWT token obtained from the login endpoint.";
        public static final String NAME_BEARER_AUTH = "bearerAuth";
        public static final String SCHEME_BEARER = "bearer";
        public static final String BEARER_FORMAT_JWT = "JWT";
        public static final String TITLE_API = "Crediya Auth API Microservice";
        public static final String VERSION_API = "1.0.0";
        public static final String DESCRIPTION_API = "This is the API for Crediya Auth Microservice";
    }
}