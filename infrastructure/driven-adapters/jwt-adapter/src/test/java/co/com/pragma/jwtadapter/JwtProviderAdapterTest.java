package co.com.pragma.jwtadapter;

import co.com.pragma.jwtadapter.config.JwtProperties;
import co.com.pragma.model.jwt.JwtData;
import co.com.pragma.model.role.Role;
import co.com.pragma.model.user.User;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.security.SignatureException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.awaitility.Awaitility.await;

class JwtProviderAdapterTest {

    private JwtProviderAdapter jwtProviderAdapter;
    private User testUser;

    @BeforeEach
    void setUp() {
        JwtProperties properties = new JwtProperties();
        properties.setSecret("VGhpcyBpcyBhIHZlcnkgc2VjdXJlIGFuZCBsb25nIHNlY3JldCBrZXkgZm9yIEpXVCBhdXRoZW50aWNhdGlvbg==");
        properties.setExpiration(3600L); // 1 hour

        jwtProviderAdapter = new JwtProviderAdapter(properties);

        testUser = User.builder()
                .name("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .idNumber("123456789")
                .role(Role.builder().rolId(1).name("ADMIN").build())
                .build();
    }

    @Test
    void generateToken_shouldCreateValidJwt() {
        String token = jwtProviderAdapter.generateToken(testUser);

        assertThat(token).isNotNull().isNotEmpty();
        assertThat(token.split("\\.")).hasSize(3);
    }

    @Test
    void getClaims_shouldParseValidToken() {
        String token = jwtProviderAdapter.generateToken(testUser);

        JwtData claims = jwtProviderAdapter.getClaims(token);

        assertThat(claims).isNotNull();
        assertThat(claims.subject()).isEqualTo(testUser.getEmail());
        assertThat(claims.role()).isEqualTo(testUser.getRole().getName());
        assertThat(claims.roleId()).isEqualTo((Integer) testUser.getRole().getRolId());
        assertThat(claims.name()).isEqualTo("John Doe");
        assertThat(claims.idNumber()).isEqualTo(testUser.getIdNumber());
    }

    @Test
    void getClaims_shouldThrowExceptionForInvalidSignature() {
        String validToken = jwtProviderAdapter.generateToken(testUser);
        String invalidToken = validToken.substring(0, validToken.lastIndexOf('.')) + ".invalidSignature";

        assertThatThrownBy(() -> jwtProviderAdapter.getClaims(invalidToken))
                .isInstanceOf(SignatureException.class);
    }

    @Test
    void getClaims_shouldThrowExceptionForExpiredToken() {
        JwtProperties shortLivedProperties = new JwtProperties();
        shortLivedProperties.setSecret("VGhpcyBpcyBhIHZlcnkgc2VjdXJlIGFuZCBsb25nIHNlY3JldCBrZXkgZm9yIEpXVCBhdXRoZW50aWNhdGlvbg==");
        shortLivedProperties.setExpiration(0L);
        JwtProviderAdapter shortLivedAdapter = new JwtProviderAdapter(shortLivedProperties);
        String expiredToken = shortLivedAdapter.generateToken(testUser);

        await().atMost(Duration.ofSeconds(1)).untilAsserted(() ->
                assertThatThrownBy(() -> jwtProviderAdapter.getClaims(expiredToken))
                        .isInstanceOf(ExpiredJwtException.class)
        );
    }
}