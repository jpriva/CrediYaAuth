package co.com.pragma.usecase.user.exceptions;

import co.com.pragma.usecase.user.constants.ErrorMessage;

public class EmailFormatException extends CustomException {
    public EmailFormatException() {
        super(ErrorMessage.EMAIL_FORMAT, ErrorMessage.EMAIL_FORMAT_CODE);
    }
}
