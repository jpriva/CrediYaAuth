package co.com.pragma.model.user.exceptions;

import co.com.pragma.model.user.constants.ErrorMessage;

public class EmailFormatException extends UserException {
    public EmailFormatException() {
        super(ErrorMessage.EMAIL_FORMAT, ErrorMessage.EMAIL_FORMAT_CODE);
    }
}
