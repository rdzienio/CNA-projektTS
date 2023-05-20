package pl.gienius.loginService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Server {

    List<ClientThread> clientList = new ArrayList<>();
    Logger logger = LoggerFactory.getLogger(Server.class);

    void runServer() throws IOException {
        ServerSocket serverSocket = new ServerSocket(3000);

        logger.info("Login service running...");

        while (true) {
            Socket socket = serverSocket.accept();
            logger.info("New connection");

            ClientThread thread = new ClientThread(socket);
            clientList.add(thread);
        }
    }

}
