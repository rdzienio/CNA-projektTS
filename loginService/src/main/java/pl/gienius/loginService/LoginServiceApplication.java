package pl.gienius.loginService;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;

@SpringBootApplication
public class LoginServiceApplication {

	public static void main(String[] args) throws IOException {
		SpringApplication.run(LoginServiceApplication.class, args);
		Server server = new Server();
		server.runServer();
	}

}
