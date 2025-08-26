package co.com.pragma.model.user.exceptions;

import co.com.pragma.model.user.constants.ErrorMessage;

public class RoleNotFoundException extends UserException {
    public RoleNotFoundException() {
        super(ErrorMessage.ROL_NOT_FOUND, ErrorMessage.ROL_NOT_FOUND_CODE);
    }
}
