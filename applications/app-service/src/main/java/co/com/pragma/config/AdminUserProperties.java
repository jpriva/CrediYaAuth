package co.com.pragma.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "app.default-admin")
public class AdminUserProperties {
    private String email;
    private String password;
}
