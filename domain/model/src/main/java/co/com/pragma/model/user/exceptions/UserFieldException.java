package co.com.pragma.model.user.exceptions;

import co.com.pragma.model.user.constants.ErrorMessage;

public class UserFieldException extends UserException {
    public UserFieldException() {
        super(ErrorMessage.REQUIRED_FIELDS, ErrorMessage.REQUIRED_FIELDS_CODE);
    }
}
