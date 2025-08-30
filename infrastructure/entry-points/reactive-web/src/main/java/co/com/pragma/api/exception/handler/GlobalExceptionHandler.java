package co.com.pragma.api.exception.handler;

import co.com.pragma.api.dto.ErrorDTO;
import co.com.pragma.model.constants.LogMessages;
import co.com.pragma.model.logs.gateways.LoggerPort;
import co.com.pragma.model.constants.ErrorMessage;
import co.com.pragma.model.exceptions.CustomException;
import org.springframework.boot.autoconfigure.web.WebProperties;
import org.springframework.boot.autoconfigure.web.reactive.error.AbstractErrorWebExceptionHandler;
import org.springframework.boot.web.reactive.error.ErrorAttributes;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.*;
import org.springframework.web.server.ServerWebInputException;
import reactor.core.publisher.Mono;

import java.time.Instant;

@Component
@Order(-2)
public class GlobalExceptionHandler extends AbstractErrorWebExceptionHandler {

    private final LoggerPort logger;

    public GlobalExceptionHandler(
            ErrorAttributes errorAttributes,
            ApplicationContext applicationContext,
            ServerCodecConfigurer configurer,
            LoggerPort logger) {
        super(errorAttributes, new WebProperties.Resources(), applicationContext);
        this.setMessageWriters(configurer.getWriters());
        this.setMessageReaders(configurer.getReaders());
        this.logger = logger;
    }

    @Override
    protected RouterFunction<ServerResponse> getRoutingFunction(ErrorAttributes errorAttributes) {
        return RouterFunctions.route(RequestPredicates.all(), this::renderErrorResponse);
    }

    private record ErrorResponse(HttpStatus status, ErrorDTO body) {}

    private Mono<ServerResponse> renderErrorResponse(ServerRequest serverRequest) {
        Throwable error = getError(serverRequest);
        logger.error(LogMessages.GLOBAL_EXCEPTION_HANDLER_ERROR, serverRequest.path(), error);

        ErrorResponse errorResponse = buildErrorResponse(error, serverRequest.path());

        return ServerResponse.status(errorResponse.status())
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(errorResponse.body());
    }

    private ErrorResponse buildErrorResponse(Throwable error, String path) {
        if (error instanceof ServerWebInputException) {
            return new ErrorResponse(
                    HttpStatus.BAD_REQUEST,
                    ErrorDTO.builder().timestamp(Instant.now()).path(path).code(ErrorMessage.FAIL_READ_REQUEST_CODE).message(ErrorMessage.FAIL_READ_REQUEST).build()
            );
        } else if (error instanceof CustomException ex) {
            HttpStatus status = HttpStatus.resolve(ex.getWebStatus());
            if (status == null) {
                status = HttpStatus.INTERNAL_SERVER_ERROR;
            }
            return new ErrorResponse(
                    status,
                    ErrorDTO.builder().timestamp(ex.getTimestamp()).path(path).code(ex.getCode()).message(ex.getMessage()).build()
            );
        } else {
            return new ErrorResponse(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    ErrorDTO.builder().timestamp(Instant.now()).path(path).code(ErrorMessage.UNKNOWN_CODE).message(ErrorMessage.UNKNOWN_ERROR).build()
            );
        }
    }
}
