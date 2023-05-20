package pl.gienius.postservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;

@SpringBootApplication
public class PostServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(PostServiceApplication.class, args);
		try {
			new Server().runServer();
		} catch (IOException e) {
			System.out.println(e);
		}
	}

}
