package co.com.pragma.model.user.exceptions;

import co.com.pragma.model.user.constants.ErrorMessage;

public class EmailTakenException extends UserException {
    public EmailTakenException() {
        super(ErrorMessage.EMAIL_TAKEN, ErrorMessage.EMAIL_TAKEN_CODE);
    }
}
