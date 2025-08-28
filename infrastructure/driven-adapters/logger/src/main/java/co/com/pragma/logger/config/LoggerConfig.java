package co.com.pragma.logger.config;

import co.com.pragma.logger.Slf4jLoggerAdapter;
import co.com.pragma.model.logs.gateways.LoggerPort;
import org.springframework.beans.factory.InjectionPoint;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

@Configuration
public class LoggerConfig {

    @Bean
    @Scope("prototype")
    public LoggerPort logger(InjectionPoint injectionPoint) {
        return new Slf4jLoggerAdapter(injectionPoint.getMember().getDeclaringClass());
    }
}