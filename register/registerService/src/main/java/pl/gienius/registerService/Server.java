package pl.gienius.registerService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.*;
import java.util.*;

public class Server {

    List<ClientThread> clientList = new ArrayList<>();
    Logger logger = LoggerFactory.getLogger(Server.class);

    void runServer() throws IOException {
        ServerSocket serverSocket = new ServerSocket(2000);

        logger.info("Register service running...");

        while (true) {
            Socket socket = serverSocket.accept();
            logger.info("New connection");

            ClientThread thread = new ClientThread(socket);
            clientList.add(thread);
        }
    }

}
