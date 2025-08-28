package co.com.pragma.usecase.user.exceptions;

import co.com.pragma.usecase.user.constants.ErrorMessage;
import lombok.Getter;

@Getter
public class FieldBlankException extends CustomException {
    public final String field;
    public FieldBlankException(String field) {
        super(field + " " + ErrorMessage.REQUIRED_FIELDS, ErrorMessage.REQUIRED_FIELDS_CODE);
        this.field = field;
    }
}
