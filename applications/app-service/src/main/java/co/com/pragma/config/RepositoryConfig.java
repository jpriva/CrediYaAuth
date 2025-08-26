package co.com.pragma.config;

import co.com.pragma.model.user.gateways.RoleRepository;
import co.com.pragma.model.user.gateways.UserRepository;
import co.com.pragma.r2dbc.RoleEntityRepository;
import co.com.pragma.r2dbc.RoleEntityRepositoryAdapter;
import co.com.pragma.r2dbc.UserEntityRepository;
import co.com.pragma.r2dbc.UserRepositoryAdapter;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RepositoryConfig {
    @Bean
    public UserRepository userRepository(UserEntityRepository repository, ObjectMapper mapper, RoleEntityRepository roleEntityRepository) {
        return new UserRepositoryAdapter(repository, mapper, roleEntityRepository);
    }

    @Bean
    public RoleRepository roleRepository(RoleEntityRepository repository, ObjectMapper mapper) {
        return new RoleEntityRepositoryAdapter(repository, mapper);
    }
}
