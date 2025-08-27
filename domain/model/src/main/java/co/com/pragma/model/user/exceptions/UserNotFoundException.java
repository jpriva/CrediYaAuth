package co.com.pragma.model.user.exceptions;

import co.com.pragma.model.user.constants.ErrorMessage;

public class UserNotFoundException extends UserException {
    public UserNotFoundException() {
        super(ErrorMessage.USER_NOT_FOUND, ErrorMessage.USER_NOT_FOUND_CODE);
    }
}
