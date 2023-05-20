package pl.gienius.clientcli;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;

@SpringBootApplication
public class ClientCliApplication {

	public static void main(String[] args) throws IOException {
		userController userController = new userController();

		SpringApplication.run(ClientCliApplication.class, args);
		userController.start();

	}

}
