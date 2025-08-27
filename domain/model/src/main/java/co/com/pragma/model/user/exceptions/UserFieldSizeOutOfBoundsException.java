package co.com.pragma.model.user.exceptions;

import co.com.pragma.model.user.constants.ErrorMessage;

public class UserFieldSizeOutOfBoundsException extends UserException {
    public final String field;
    public UserFieldSizeOutOfBoundsException(String field) {
        super(field + " " +ErrorMessage.SIZE_OUT_OF_BOUNDS, ErrorMessage.SIZE_OUT_OF_BOUNDS_CODE);
        this.field = field;
    }
}
