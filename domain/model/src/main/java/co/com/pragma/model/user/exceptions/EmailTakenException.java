package co.com.pragma.model.user.exceptions;

public class EmailTakenException extends UserException {
    public EmailTakenException() {
        super(ErrorMessage.EMAIL_TAKEN);
    }
}
