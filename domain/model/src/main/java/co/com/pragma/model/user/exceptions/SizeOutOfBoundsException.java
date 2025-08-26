package co.com.pragma.model.user.exceptions;

import co.com.pragma.model.user.constants.ErrorMessage;

public class SizeOutOfBoundsException extends UserException {
    public SizeOutOfBoundsException() {
        super(ErrorMessage.SIZE_OUT_OF_BOUNDS, ErrorMessage.SIZE_OUT_OF_BOUNDS_CODE);
    }
}
