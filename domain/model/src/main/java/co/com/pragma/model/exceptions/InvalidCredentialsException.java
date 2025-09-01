package co.com.pragma.model.exceptions;

import co.com.pragma.model.constants.ErrorMessage;

import java.net.HttpURLConnection;

public class InvalidCredentialsException extends CustomException {

    public InvalidCredentialsException() {
        super(ErrorMessage.INVALID_CREDENTIALS, ErrorMessage.INVALID_CREDENTIALS_CODE, HttpURLConnection.HTTP_UNAUTHORIZED);
    }
}
