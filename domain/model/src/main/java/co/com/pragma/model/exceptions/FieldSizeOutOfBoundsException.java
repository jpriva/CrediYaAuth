package co.com.pragma.model.exceptions;

import co.com.pragma.model.constants.ErrorMessage;

public class FieldSizeOutOfBoundsException extends CustomException {
    public final String field;
    public FieldSizeOutOfBoundsException(String field) {
        super(field + " " +ErrorMessage.SIZE_OUT_OF_BOUNDS, ErrorMessage.SIZE_OUT_OF_BOUNDS_CODE);
        this.field = field;
    }
}
