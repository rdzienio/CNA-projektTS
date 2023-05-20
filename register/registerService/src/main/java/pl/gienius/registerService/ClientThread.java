package pl.gienius.registerService;

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
                    out.println("400;Wrong registration data.");
                    return;
                }
                logger.info("From client: " + data);
                String[] requestParts = data.split(";", 3);
                String requestType = requestParts[0];
                if (!requestType.equals("register_request")) {
                    out.println("400;Wrong registration data.");
                    return;
                }
                String user = requestParts[1];
                String passwd = requestParts[2];

                try (Connection connection = dbClient.getConnection()) {
                    PreparedStatement checkUserStatement = connection.prepareStatement("SELECT * FROM users WHERE username = ?");
                    checkUserStatement.setString(1, user);
                    logger.info("Checking database...");
                    ResultSet resultSet = checkUserStatement.executeQuery();

                    if (resultSet.next()) {
                        logger.info("409;User already existing in DB.");
                        out.println("409;User already existing in DB.");
                        out.flush();
                        return;
                    }

                    PreparedStatement insertUserStatement = connection.prepareStatement("INSERT INTO users (username, password) VALUES (?, ?)");
                    insertUserStatement.setString(1, user);
                    insertUserStatement.setString(2, passwd);
                    logger.info("Updating database...");
                    insertUserStatement.executeUpdate();

                    out.println("201;Successfully registered. Congratulations. ");
                    out.flush();
                } catch (SQLException e) {
                    logger.error("Registration service ERROR. " + e.getMessage());
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
