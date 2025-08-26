package co.com.pragma.model.user.exceptions;

import co.com.pragma.model.user.constants.ErrorMessage;

public class SalaryUnboundException extends UserException {
    public SalaryUnboundException() {
        super(ErrorMessage.SALARY_UNBOUND, ErrorMessage.SALARY_UNBOUND_CODE);
    }
}
