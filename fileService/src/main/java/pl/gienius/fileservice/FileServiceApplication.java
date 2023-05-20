package pl.gienius.fileservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;

@SpringBootApplication
public class FileServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(FileServiceApplication.class, args);
		try {
			new Server().runServer();
		} catch (IOException e) {
			System.out.println(e);
		}
	}

}
