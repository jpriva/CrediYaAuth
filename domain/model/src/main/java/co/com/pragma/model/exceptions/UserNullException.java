package co.com.pragma.model.exceptions;

import co.com.pragma.model.constants.ErrorMessage;

public class UserNullException extends CustomException {
    public UserNullException() {
        super(ErrorMessage.USER_NULL, ErrorMessage.USER_NULL_CODE);
    }
}
