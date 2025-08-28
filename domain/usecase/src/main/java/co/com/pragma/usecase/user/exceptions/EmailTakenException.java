package co.com.pragma.usecase.user.exceptions;

import co.com.pragma.usecase.user.constants.ErrorMessage;

import static java.net.HttpURLConnection.HTTP_CONFLICT;

public class EmailTakenException extends CustomException {
    public EmailTakenException() {
        super(ErrorMessage.EMAIL_TAKEN, ErrorMessage.EMAIL_TAKEN_CODE, HTTP_CONFLICT);
    }
}
