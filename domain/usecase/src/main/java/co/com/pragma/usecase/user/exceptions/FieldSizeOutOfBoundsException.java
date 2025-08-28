package co.com.pragma.usecase.user.exceptions;

import co.com.pragma.usecase.user.constants.ErrorMessage;

public class FieldSizeOutOfBoundsException extends CustomException {
    public final String field;
    public FieldSizeOutOfBoundsException(String field) {
        super(field + " " +ErrorMessage.SIZE_OUT_OF_BOUNDS, ErrorMessage.SIZE_OUT_OF_BOUNDS_CODE);
        this.field = field;
    }
}
