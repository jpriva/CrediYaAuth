package co.com.pragma.model.exceptions;

import co.com.pragma.model.constants.ErrorMessage;

public class FilterEmptyException extends CustomException {
    public FilterEmptyException() {
        super(ErrorMessage.FILTER_IS_EMPTY, ErrorMessage.FILTER_IS_EMPTY_CODE);
    }
}
