package co.com.pragma.model.user.exceptions;

import lombok.Getter;

@Getter
public class UserException extends RuntimeException {
    private final String code;
    public UserException(String message,String code) {
        super(message);
        this.code = code;
    }
}
