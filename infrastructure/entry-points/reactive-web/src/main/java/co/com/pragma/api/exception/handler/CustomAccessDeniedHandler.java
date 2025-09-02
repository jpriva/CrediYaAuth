package co.com.pragma.api.exception.handler;

import co.com.pragma.api.dto.ErrorDTO;
import co.com.pragma.model.constants.ErrorMessage;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.server.authorization.ServerAccessDeniedHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.time.Instant;

@Component
@RequiredArgsConstructor
public class CustomAccessDeniedHandler implements ServerAccessDeniedHandler {

    private final ObjectMapper objectMapper;

    @Override
    public Mono<Void> handle(ServerWebExchange exchange, AccessDeniedException denied) {
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
}