package co.com.pragma.model.exceptions;

import co.com.pragma.model.constants.ErrorMessage;

public class RoleNotFoundException extends CustomException {
    public RoleNotFoundException() {
        super(ErrorMessage.ROL_NOT_FOUND, ErrorMessage.ROL_NOT_FOUND_CODE);
    }
}
