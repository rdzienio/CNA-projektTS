package pl.gienius.postservice;

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
                    out.println("400;Wrong post request.");
                    return;
                }
                logger.info("From client: " + data);
                String[] requestData = data.split(";");
                String requestType = requestData[0];

                switch (requestType) {
                    case "post_put": {
                        try (Connection connection = dbClient.getConnection()) {
                            String username = requestData[1];
                            String postData = requestData[2];
                            PreparedStatement getUserIdStatement = connection.prepareStatement("SELECT id FROM users WHERE username = ?");
                            getUserIdStatement.setString(1, username);
                            ResultSet resultSet = getUserIdStatement.executeQuery();

                            if (!resultSet.next()) {
                                out.println("401;User doesn't exist in DB.");
                                out.flush();
                                return;
                            }
                            int userId = resultSet.getInt("id");

                            PreparedStatement insertPostStatement = connection.prepareStatement("INSERT INTO posts (user, content) VALUES (?, ?)");
                            insertPostStatement.setInt(1, userId);
                            insertPostStatement.setString(2, postData);
                            insertPostStatement.executeUpdate();

                            out.println("200;Post successfully added.");
                            out.flush();
                        } catch (SQLException e) {
                            System.err.println("Post service ERROR. " + e.getMessage());
                        }
                        break;
                    }
                    case "post_get": {
                        try (Connection connection = dbClient.getConnection()) {
                            PreparedStatement returnPostStatement = connection.prepareStatement("SELECT * FROM posts ORDER BY ID DESC LIMIT 10");
                            ResultSet resultSet = returnPostStatement.executeQuery();
                            logger.info("Got posts from DB!");

                            StringBuilder posts = new StringBuilder();
                            while (resultSet.next()) {
                                String content = resultSet.getString("content");
                                int user = resultSet.getInt("user");
                                String timeStamp = resultSet.getString("tstamp");
                                logger.info("Got: " + content + "; " + user + "; " + timeStamp);

                                PreparedStatement getUsernameStatement = connection.prepareStatement("SELECT username FROM users WHERE id = ?");
                                getUsernameStatement.setInt(1, user);
                                ResultSet userResultSet = getUsernameStatement.executeQuery();

                                if (userResultSet.next()) {
                                    String username = userResultSet.getString("username");
                                    posts.append("@").append(username).append("\s\sput:\t").append(content).append("\tat: ").append(timeStamp).append("\t%\t\t%\t");
                                    //logger.info(posts.toString());
                                }
                            }
                            out.println("200;" + posts.toString());
                            out.flush();
                        } catch (SQLException e) {
                            System.err.println("Post service ERROR. " + e.getMessage());
                        }
                        break;
                    }
                    default:
                        out.println("400;Wrong post request.");
                        return;
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
