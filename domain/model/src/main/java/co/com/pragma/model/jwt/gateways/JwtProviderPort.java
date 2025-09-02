package co.com.pragma.model.jwt.gateways;

import co.com.pragma.model.jwt.JwtData;
import co.com.pragma.model.user.User;

public interface JwtProviderPort {

    String generateToken(User user);
    JwtData getClaims(String token);
}
