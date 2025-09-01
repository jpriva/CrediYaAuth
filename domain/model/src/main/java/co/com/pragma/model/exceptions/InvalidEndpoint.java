package co.com.pragma.model.exceptions;

import co.com.pragma.model.constants.ErrorMessage;

import java.net.HttpURLConnection;

public class InvalidEndpoint extends CustomException {
    public InvalidEndpoint() {
        super(ErrorMessage.INVALID_ENDPOINT, ErrorMessage.INVALID_ENDPOINT_CODE, HttpURLConnection.HTTP_NOT_FOUND);
    }
}
