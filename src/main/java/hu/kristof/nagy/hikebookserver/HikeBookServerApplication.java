package hu.kristof.nagy.hikebookserver;

import hu.kristof.nagy.hikebookserver.data.UserSource;
import hu.kristof.nagy.hikebookserver.service.Login;
import hu.kristof.nagy.hikebookserver.service.Register;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class HikeBookServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(HikeBookServerApplication.class, args);
	}

	@Bean
	public Login provideLogin() {
		return new Login();
	}

	@Bean
	public Register provideRegister() {
		return new Register();
	}

	@Bean
	public UserSource provideUserSource() {
		UserSource userSource = new UserSource();
		userSource.init();
		return userSource;
	}
}
