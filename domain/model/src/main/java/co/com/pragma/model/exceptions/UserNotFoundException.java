package co.com.pragma.model.exceptions;

import co.com.pragma.model.constants.ErrorMessage;

public class UserNotFoundException extends CustomException {
    public UserNotFoundException() {
        super(ErrorMessage.USER_NOT_FOUND, ErrorMessage.USER_NOT_FOUND_CODE);
    }
}
