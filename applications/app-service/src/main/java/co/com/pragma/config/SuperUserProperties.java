package co.com.pragma.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "app.default-super-user")
public class SuperUserProperties {
    private String email;
    private String password;
}
