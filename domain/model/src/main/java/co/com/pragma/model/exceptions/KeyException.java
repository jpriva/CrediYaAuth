package co.com.pragma.model.exceptions;

import co.com.pragma.model.constants.ErrorMessage;

import java.net.HttpURLConnection;

public class KeyException extends CustomException {

    public KeyException() {
        super(ErrorMessage.INVALID_KEY, ErrorMessage.INVALID_KEY_CODE, HttpURLConnection.HTTP_INTERNAL_ERROR);
    }
}
