package hu.kristof.nagy.hikebookserver.config;

import hu.kristof.nagy.hikebookserver.service.Login;
import hu.kristof.nagy.hikebookserver.service.Register;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AuthConfig {
    @Bean
    public Login provideLogin() {
        return new Login();
    }

    @Bean
    public Register provideRegister() {
        return new Register();
    }
}
