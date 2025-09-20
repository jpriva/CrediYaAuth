package co.com.pragma.model.exceptions;

import co.com.pragma.model.constants.ErrorMessage;

import static java.net.HttpURLConnection.HTTP_CONFLICT;

public class IdNumberTakenException extends CustomException {
    public IdNumberTakenException() {
        super(ErrorMessage.ID_NUMBER_TAKEN, ErrorMessage.ID_NUMBER_TAKEN_CODE, HTTP_CONFLICT);
    }
}
