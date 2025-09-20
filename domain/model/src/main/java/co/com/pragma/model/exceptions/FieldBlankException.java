package co.com.pragma.model.exceptions;

import co.com.pragma.model.constants.ErrorMessage;
import lombok.Getter;

@Getter
public class FieldBlankException extends CustomException {
    public final String field;
    public FieldBlankException(String field) {
        super(field + " " + ErrorMessage.REQUIRED_FIELDS, ErrorMessage.REQUIRED_FIELDS_CODE);
        this.field = field;
    }
}
