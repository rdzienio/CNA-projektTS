package pl.gienius.fileservice;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;

public class ClientThread extends Thread {

    Socket socket;
    BufferedReader in;
    PrintStream out;

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
                    out.println("400;Wrong file request.");
                    return;
                }
                logger.info("From client: " + data);
                String[] requestData = data.split(";");
                String requestType = requestData[0];

                switch (requestType) {
                    case "upload_request": {
                        String destinationFileName = requestData[2];
                        String encodedFile = requestData[3];
                        byte[] fileBytes = Base64.getDecoder().decode(encodedFile);

                        String destinationPath = System.getProperty("user.home") + File.separator + destinationFileName;
                        Files.write(Paths.get(destinationPath), fileBytes);
                        out.println("200;File uploaded.");
                        break;
                    }
                    case "download_request": {
                        String fileName = requestData[2];
                        String filePath = System.getProperty("user.home") + File.separator + fileName;

                        Path path = Paths.get(filePath);
                        if (Files.exists(path)) {
                            byte[] fileBytes = Files.readAllBytes(path);
                            String encodedFile = Base64.getEncoder().encodeToString(fileBytes);
                            out.println("200;" + encodedFile);
                        } else {
                            out.println("404;File not found.");
                        }
                        break;
                    }
                    default:
                        out.println("400;Wrong file request.");
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
