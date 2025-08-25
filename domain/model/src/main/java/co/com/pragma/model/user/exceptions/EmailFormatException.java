package co.com.pragma.model.user.exceptions;

public class EmailFormatException extends UserException {
    public EmailFormatException() {
        super(ErrorMessage.EMAIL_FORMAT, ErrorMessage.EMAIL_FORMAT_CODE);
    }
}
