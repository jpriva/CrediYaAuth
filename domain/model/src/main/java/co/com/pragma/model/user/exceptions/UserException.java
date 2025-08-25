package co.com.pragma.model.user.exceptions;

public class UserException extends RuntimeException {
    private final String code;
    public UserException(String message,String code) {
        super(message);
        this.code = code;
    }
    public UserException(String message) {
        super(message);
        this.code = "UNKNOWN";
    }
    public String getCode() {
        return code;
    }
}
