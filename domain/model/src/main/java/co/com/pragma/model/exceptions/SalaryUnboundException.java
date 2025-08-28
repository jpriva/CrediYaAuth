package co.com.pragma.model.exceptions;

import co.com.pragma.model.constants.ErrorMessage;

public class SalaryUnboundException extends CustomException {
    public SalaryUnboundException() {
        super(ErrorMessage.SALARY_UNBOUND, ErrorMessage.SALARY_UNBOUND_CODE);
    }
}
