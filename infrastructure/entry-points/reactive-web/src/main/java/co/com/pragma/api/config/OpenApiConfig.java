package co.com.pragma.api.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.context.annotation.Configuration;
import co.com.pragma.api.constants.ApiConstants.ApiConfig;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = ApiConfig.TITLE_API,
                version = ApiConfig.VERSION_API,
                description = ApiConfig.DESCRIPTION_API
        ),
        security = @SecurityRequirement(name = ApiConfig.NAME_BEARER_AUTH)
)
@SecurityScheme(
        name = ApiConfig.NAME_BEARER_AUTH,
        description = ApiConfig.DESCRIPTION_BEARER_AUTH,
        scheme = ApiConfig.SCHEME_BEARER,
        type = SecuritySchemeType.HTTP,
        bearerFormat = ApiConfig.BEARER_FORMAT_JWT,
        in = SecuritySchemeIn.HEADER
)
public class OpenApiConfig {
}