package co.com.pragma.model.exceptions;

import co.com.pragma.model.constants.ErrorMessage;

public class EmailFormatException extends CustomException {
    public EmailFormatException() {
        super(ErrorMessage.EMAIL_FORMAT, ErrorMessage.EMAIL_FORMAT_CODE);
    }
}
