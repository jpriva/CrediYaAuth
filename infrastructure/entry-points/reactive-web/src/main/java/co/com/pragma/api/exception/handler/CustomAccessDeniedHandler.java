package co.com.pragma.api.exception.handler;

import co.com.pragma.api.dto.ErrorDTO;
import co.com.pragma.model.constants.ErrorMessage;
import co.com.pragma.model.logs.gateways.LoggerPort;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.server.authorization.ServerAccessDeniedHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.net.InetSocketAddress;
import java.time.Instant;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class CustomAccessDeniedHandler implements ServerAccessDeniedHandler {

    private final ObjectMapper objectMapper;
    private final LoggerPort logger;

    @Override
    public Mono<Void> handle(ServerWebExchange exchange, AccessDeniedException denied) {

        String path = exchange.getRequest().getPath().value();
        InetSocketAddress remoteAddress = exchange.getRequest().getRemoteAddress();
        String clientIp = remoteAddress != null ? remoteAddress.getAddress().getHostAddress() : "unknown";

        logger.warn("Access denied path=[{}] ip=[{}] reason=[{}]",
                path, clientIp, denied.getMessage());

        logAuthDetails(exchange);

        exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
        exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);

        ErrorDTO errorDTO = ErrorDTO.builder()
                .timestamp(Instant.now())
                .path(exchange.getRequest().getPath().value())
                .code(ErrorMessage.ACCESS_DENIED_CODE)
                .message(ErrorMessage.ACCESS_DENIED)
                .build();

        DataBufferFactory bufferFactory = exchange.getResponse().bufferFactory();
        return exchange.getResponse().writeWith(Mono.fromCallable(() -> bufferFactory.wrap(objectMapper.writeValueAsBytes(errorDTO))));
    }


    private void logAuthDetails(ServerWebExchange exchange) {
        String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            // Huella no reversible del token (NO loguear el JWT completo)
            String token = authHeader.substring(7);
            String fingerprint = Integer.toHexString(token.hashCode());
            logger.debug("Denied token fingerprint=[{}]", fingerprint);
        } else {
            logger.debug("No Bearer token present");
        }

        exchange.getPrincipal()
                .doOnNext(principal -> {
                    if (principal instanceof Authentication authentication) {
                        String authorities = authentication.getAuthorities()
                                .stream()
                                .map(GrantedAuthority::getAuthority)
                                .collect(Collectors.joining(","));
                        logger.debug("Denied authentication name=[{}] authenticated=[{}] authorities=[{}] details=[{}]",
                                authentication.getName(),
                                authentication.isAuthenticated(),
                                authorities,
                                authentication.getDetails());
                    } else {
                        logger.debug("Denied principal type=[{}] name=[{}]",
                                principal.getClass().getSimpleName(),
                                principal.getName());
                    }
                })
                .switchIfEmpty(Mono.fromRunnable(() ->
                        logger.debug("Denied request without principal (anonymous)")))
                .subscribe(); // Side-effect logging
    }

    private byte[] writeBytes(ErrorDTO dto) throws RuntimeException {
        try {
            return objectMapper.writeValueAsBytes(dto);
        } catch (Exception e) {
            throw new RuntimeException("Error serializando ErrorDTO", e);
        }
    }
}