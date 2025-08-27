package co.com.pragma.model.user.exceptions;

import co.com.pragma.model.user.constants.ErrorMessage;
import lombok.Getter;

@Getter
public class UserFieldBlankException extends UserException {
    public final String field;
    public UserFieldBlankException(String field) {
        super(field + " " + ErrorMessage.REQUIRED_FIELDS, ErrorMessage.REQUIRED_FIELDS_CODE);
        this.field = field;
    }
}
