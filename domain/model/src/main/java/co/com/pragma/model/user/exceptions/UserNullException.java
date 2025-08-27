package co.com.pragma.model.user.exceptions;

import co.com.pragma.model.user.constants.ErrorMessage;

public class UserNullException extends UserException {
    public UserNullException() {
        super(ErrorMessage.USER_NULL, ErrorMessage.USER_NULL_CODE);
    }
}
