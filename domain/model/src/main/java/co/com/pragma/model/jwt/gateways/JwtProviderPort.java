package co.com.pragma.model.jwt.gateways;

import co.com.pragma.model.jwt.JwtData;
import co.com.pragma.model.user.User;

import java.security.PublicKey;

public interface JwtProviderPort {

    String generateToken(User user);
    JwtData getClaims(String token);
    PublicKey getPublicKey() throws Exception;
}
