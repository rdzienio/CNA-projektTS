package pl.gienius.loginService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ClientThread extends Thread {

    Socket socket;
    BufferedReader in;
    PrintStream out;
    dbClient dbClient = new dbClient();

    Logger logger = LoggerFactory.getLogger(ClientThread.class);

    public ClientThread(Socket clientSocket) throws IOException {
        this.socket = clientSocket;
        this.in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        this.out = new PrintStream(clientSocket.getOutputStream());
        logger.info("New Thread");
        start();

    }

    @Override
    public void run() {
        try {
            while (isInterrupted() == false) {
                String data = in.readLine();
                if (data == null) {
                    out.println("400;Wrong login data.");
                    return;
                }
                logger.info("From client: " + data);
                String[] requestParts = data.split(";", 3);
                String requestType = requestParts[0];
                if (!requestType.equals("login_request")) {
                    out.println("400;Wrong login data.");
                    return;
                }
                String user = requestParts[1];
                String passwd = requestParts[2];

                try (Connection connection = dbClient.getConnection()) {
                    PreparedStatement checkUserStatement = connection.prepareStatement("SELECT * FROM users WHERE username = ?");
                    checkUserStatement.setString(1, user);
                    ResultSet resultSet = checkUserStatement.executeQuery();

                    if (resultSet.next()) {

                        if (resultSet.getString("password").equals(passwd)) {
                            out.println("200;Logged in.");
                        } else {
                            out.println("401;Wrong password.");
                        }
                    } else {
                        out.println("404;User doesn't exist in DB.");
                    }
                } catch (SQLException e) {
                    logger.error("Login service ERROR. " + e.getMessage());
                }
            }
        } catch (IOException e) {
            logger.error(e.getMessage());
        } finally {
            try {
                in.close();
                out.close();
                socket.close();
            } catch (IOException e) {
                logger.error("Stream is broken\n" + e.getMessage());
            }
        }
    }
}
