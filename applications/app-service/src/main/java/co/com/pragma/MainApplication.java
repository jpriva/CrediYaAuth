package co.com.pragma;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories;

@SpringBootApplication
@ConfigurationPropertiesScan
@EnableR2dbcRepositories(basePackages = "co.com.pragma.r2dbc")
@ComponentScan(basePackages = {
        "co.com.pragma.api",
        "co.com.pragma.config",
        "co.com.pragma.logger",
        "co.com.pragma.r2dbc",
        "co.com.pragma.usecase"

})
public class MainApplication {
    public static void main(String[] args) {
        SpringApplication.run(MainApplication.class, args);
    }
}
