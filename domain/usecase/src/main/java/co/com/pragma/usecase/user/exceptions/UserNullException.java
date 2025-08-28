package co.com.pragma.usecase.user.exceptions;

import co.com.pragma.usecase.user.constants.ErrorMessage;

public class UserNullException extends CustomException {
    public UserNullException() {
        super(ErrorMessage.USER_NULL, ErrorMessage.USER_NULL_CODE);
    }
}
