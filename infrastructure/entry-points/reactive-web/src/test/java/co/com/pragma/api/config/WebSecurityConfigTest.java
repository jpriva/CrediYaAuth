package co.com.pragma.api.config;

import co.com.pragma.api.constants.ApiConstants;
import co.com.pragma.api.exception.handler.CustomAccessDeniedHandler;
import co.com.pragma.api.exception.handler.GlobalExceptionHandler;
import co.com.pragma.model.jwt.JwtData;
import co.com.pragma.model.jwt.gateways.JwtProviderPort;
import co.com.pragma.model.logs.gateways.LoggerPort;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.security.SignatureException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;

import static org.mockito.Mockito.when;

@WebFluxTest
@ContextConfiguration(classes = {
        WebSecurityConfig.class, CustomAccessDeniedHandler.class,
        GlobalExceptionHandler.class
})
class WebSecurityConfigTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean
    private JwtProviderPort jwtProvider;

    @MockitoBean
    private LoggerPort logger;

    @ParameterizedTest
    @ValueSource(strings = {
            "/v3/api-docs",
            "/swagger-ui.html"
    })
    void publicEndpoints_shouldBeAccessibleWithoutAuth(String publicPath) {
        webTestClient.get()
                .uri(publicPath)
                .exchange()
                .expectStatus().isNotFound(); // 404 is expected because no real handler exists in this test slice
    }

    @Test
    void loginEndpoint_shouldBeAccessibleWithoutAuth() {
        webTestClient.post()
                .uri(ApiConstants.ApiPaths.LOGIN_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("{}")
                .exchange()
                .expectStatus().isNotFound(); // 404 is correct, it means security passed
    }

    @Test
    void protectedEndpoint_shouldReturnUnauthorized_whenNoTokenIsProvided() {
        webTestClient.post()
                .uri(ApiConstants.ApiPaths.USERS_PATH)
                .exchange()
                .expectStatus().isUnauthorized();
    }

    @Test
    void protectedEndpoint_shouldReturnUnauthorized_whenTokenIsInvalid() {
        String invalidToken = "invalid-jwt";
        when(jwtProvider.getClaims(invalidToken)).thenThrow(new SignatureException("Invalid signature"));

        webTestClient.post()
                .uri(ApiConstants.ApiPaths.USERS_PATH)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + invalidToken)
                .exchange()
                .expectStatus().isUnauthorized();
    }

    @Test
    void protectedEndpoint_shouldReturnForbidden_whenUserHasInsufficientAuthority() {
        String token = "valid-token-for-client";
        JwtData jwtData = new JwtData("client@example.com", "CLIENTE", 3, "Client User", "987654321");
        when(jwtProvider.getClaims(token)).thenReturn(jwtData);

        webTestClient.post()
                .uri(ApiConstants.ApiPaths.USERS_PATH)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .exchange()
                .expectStatus().isForbidden();
    }

    @Test
    void protectedEndpoint_shouldAllowAccess_whenUserHasSufficientAuthority() {
        String token = "valid-token-for-admin";
        JwtData jwtData = new JwtData("admin@example.com", "ADMIN", 1, "Admin User", "123456789");
        when(jwtProvider.getClaims(token)).thenReturn(jwtData);

        webTestClient.post()
                .uri(ApiConstants.ApiPaths.USERS_PATH)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .exchange()
                .expectStatus().isNotFound(); // 404 is correct, it means security passed but no handler was found
    }

    @Test
    void protectedEndpoint_shouldReturnUnauthorized_whenTokenIsExpired() {
        String expiredToken = "expired-jwt";
        when(jwtProvider.getClaims(expiredToken)).thenThrow(new ExpiredJwtException(null, null, "Expired"));

        webTestClient.post()
                .uri(ApiConstants.ApiPaths.USERS_PATH)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + expiredToken)
                .exchange()
                .expectStatus().isUnauthorized();
    }
}