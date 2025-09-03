package co.com.pragma.api;

import co.com.pragma.api.constants.ApiConstants;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.*;

@Configuration
public class RouterRest {
    @Bean
    public RouterFunction<ServerResponse> routerFunction(Handler handler) {
        return RouterFunctions.route(
                POST(ApiConstants.ApiPaths.USERS_PATH).and(accept(MediaType.APPLICATION_JSON)),
                handler::listenPOSTSaveUserUseCase
        ).andRoute(
                POST(ApiConstants.ApiPaths.LOGIN_PATH).and(accept(MediaType.APPLICATION_JSON)),
                handler::listenPOSTLoginUseCase
        ).andRoute(
                GET(ApiConstants.ApiPaths.USER_BY_ID_NUMBER_PATH),
                handler::listenGETUserByIdNumberUseCase
        ).andRoute(
                POST(ApiConstants.ApiPaths.USERS_BY_EMAIL_PATH),
                handler::listenPOSTUsersByEmailUseCase
        );
    }
}
