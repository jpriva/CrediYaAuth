package co.com.pragma.usecase.user.exceptions;

import co.com.pragma.usecase.user.constants.ErrorMessage;

public class RoleNotFoundException extends CustomException {
    public RoleNotFoundException() {
        super(ErrorMessage.ROL_NOT_FOUND, ErrorMessage.ROL_NOT_FOUND_CODE);
    }
}
