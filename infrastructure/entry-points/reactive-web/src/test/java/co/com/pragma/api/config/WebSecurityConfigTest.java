package co.com.pragma.api.config;

import co.com.pragma.api.constants.ApiConstants;
import co.com.pragma.model.jwt.JwtData;
import co.com.pragma.model.jwt.gateways.JwtProviderPort;
import io.jsonwebtoken.security.SignatureException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;

import static org.mockito.Mockito.when;

@WebFluxTest
@ContextConfiguration(classes = WebSecurityConfig.class)
class WebSecurityConfigTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean
    private JwtProviderPort jwtProvider;

    @ParameterizedTest
    @ValueSource(strings = {
            ApiConstants.ApiPathMatchers.LOGIN_MATCHER,
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
    void protectedEndpoint_shouldReturnUnauthorized_whenNoTokenIsProvided() {
        webTestClient.post()
                .uri(ApiConstants.ApiPathMatchers.USER_MATCHER)
                .exchange()
                .expectStatus().isUnauthorized();
    }

    @Test
    void protectedEndpoint_shouldReturnUnauthorized_whenTokenIsInvalid() {
        String invalidToken = "invalid-jwt";
        when(jwtProvider.getClaims(invalidToken)).thenThrow(new SignatureException("Invalid signature"));

        webTestClient.post()
                .uri(ApiConstants.ApiPathMatchers.USER_MATCHER)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + invalidToken)
                .exchange()
                .expectStatus().isUnauthorized();
    }

    @Test
    void protectedEndpoint_shouldReturnUnauthorized_whenUserHasInsufficientAuthority() {
        String token = "valid-token-for-client";
        JwtData jwtData = new JwtData("client@example.com", "CLIENTE", 3, "Client User", "987654321");
        when(jwtProvider.getClaims(token)).thenReturn(jwtData);

        webTestClient.post()
                .uri(ApiConstants.ApiPathMatchers.USER_MATCHER)
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
                .uri(ApiConstants.ApiPathMatchers.USER_MATCHER)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .exchange()
                .expectStatus().isNotFound(); // 404 is correct, it means security passed but no handler was found
    }
}