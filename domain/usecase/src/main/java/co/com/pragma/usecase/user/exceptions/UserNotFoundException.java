package co.com.pragma.usecase.user.exceptions;

import co.com.pragma.usecase.user.constants.ErrorMessage;

public class UserNotFoundException extends CustomException {
    public UserNotFoundException() {
        super(ErrorMessage.USER_NOT_FOUND, ErrorMessage.USER_NOT_FOUND_CODE);
    }
}
