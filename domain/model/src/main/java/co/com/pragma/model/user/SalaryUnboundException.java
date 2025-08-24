package co.com.pragma.model.user;

import co.com.pragma.model.user.exceptions.ErrorMessage;
import co.com.pragma.model.user.exceptions.UserException;

public class SalaryUnboundException extends UserException {
    public SalaryUnboundException() {
        super(ErrorMessage.SALARY_UNBOUND);
    }
}
