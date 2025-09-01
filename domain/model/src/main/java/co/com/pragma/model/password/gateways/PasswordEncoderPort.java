package co.com.pragma.model.password.gateways;

public interface PasswordEncoderPort {

    String encode(String rawPassword);
    boolean matches(String rawPassword, String encodedPassword);
}