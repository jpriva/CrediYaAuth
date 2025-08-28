package co.com.pragma.usecase.user.exceptions;

import co.com.pragma.usecase.user.constants.ErrorMessage;

public class SalaryUnboundException extends CustomException {
    public SalaryUnboundException() {
        super(ErrorMessage.SALARY_UNBOUND, ErrorMessage.SALARY_UNBOUND_CODE);
    }
}
