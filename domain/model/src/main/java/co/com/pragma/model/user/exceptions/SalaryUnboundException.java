package co.com.pragma.model.user.exceptions;

public class SalaryUnboundException extends UserException {
    public SalaryUnboundException() {
        super(ErrorMessage.SALARY_UNBOUND);
    }
}
