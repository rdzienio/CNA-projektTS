package pl.gienius.registerService;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;

@SpringBootApplication
public class RegisterServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(RegisterServiceApplication.class, args);
        try {
            new Server().runServer();
        } catch (IOException e) {
            System.out.println(e);
        }
    }

}
