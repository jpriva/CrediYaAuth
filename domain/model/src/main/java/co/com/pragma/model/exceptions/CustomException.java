package co.com.pragma.model.exceptions;

import lombok.Getter;

import java.time.Instant;

import static java.net.HttpURLConnection.HTTP_BAD_REQUEST;

@Getter
public class CustomException extends RuntimeException {
    private final String code;
    private final int webStatus;
    private final Instant timestamp;

    public CustomException(String message, String code) {
        super(message);
        this.code = code;
        this.webStatus = HTTP_BAD_REQUEST;
        this.timestamp = Instant.now();
    }
    public CustomException(String message, String code, int webStatus) {
        super(message);
        this.code = code;
        this.webStatus = webStatus;
        this.timestamp = Instant.now();
    }
}
