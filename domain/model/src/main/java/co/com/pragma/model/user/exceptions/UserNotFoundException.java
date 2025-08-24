package co.com.pragma.model.user.exceptions;

public class UserNotFoundException extends UserException {
    public UserNotFoundException() {
        super(ErrorMessage.USER_NOT_FOUND);
    }
}
