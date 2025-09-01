package co.com.pragma.api.config;

import co.com.pragma.api.constants.ApiConstants;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.reactive.ReactiveSecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;

@WebFluxTest(excludeAutoConfiguration = ReactiveSecurityAutoConfiguration.class)
@ContextConfiguration(classes = {SecurityHeadersConfig.class, SecurityHeadersConfigTest.TestRouter.class})
class SecurityHeadersConfigTest {

    @Autowired
    private WebTestClient webTestClient;

    @TestConfiguration
    static class TestRouter {
        @Bean
        public RouterFunction<ServerResponse> testRoute() {
            return RouterFunctions.route(GET(ApiConstants.ApiPathMatchers.TEST_MATCHER),
                    request -> ServerResponse.ok().build());
        }
    }

    @Test
    void filter_shouldAddAllSecurityHeadersToResponse() {
        webTestClient.get()
                .uri(ApiConstants.ApiPathMatchers.TEST_MATCHER)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().valueEquals("Content-Security-Policy", "default-src 'self'; frame-ancestors 'self'; form-action 'self'")
                .expectHeader().valueEquals("Strict-Transport-Security", "max-age=31536000;")
                .expectHeader().valueEquals("X-Content-Type-Options", "nosniff")
                .expectHeader().valueEquals("Server", "")
                .expectHeader().valueEquals("Cache-Control", "no-store")
                .expectHeader().valueEquals("Pragma", "no-cache")
                .expectHeader().valueEquals("Referrer-Policy", "strict-origin-when-cross-origin");
    }
}