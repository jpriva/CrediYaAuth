package co.com.pragma.model.user.exceptions;

import lombok.Getter;

import static java.net.HttpURLConnection.HTTP_BAD_REQUEST;

@Getter
public class UserException extends RuntimeException {
    private final String code;
    private final int webStatus;
    public UserException(String message,String code) {
        super(message);
        this.code = code;
        this.webStatus = HTTP_BAD_REQUEST;
    }
    public UserException(String message,String code,int webStatus) {
        super(message);
        this.code = code;
        this.webStatus = webStatus;
    }
}
