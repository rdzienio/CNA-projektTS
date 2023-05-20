package pl.gienius.clientcli;

import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

@Service
public class ApiService {
    String serverIP = "localhost";
    int serverPort = 40173;

    //String in;
    //String out;

    Socket socket = new Socket(serverIP, serverPort);

    DataOutputStream doSerwera = new DataOutputStream(socket.getOutputStream());

    BufferedReader odSerwera = new BufferedReader(new InputStreamReader(socket.getInputStream()));


    public ApiService() throws IOException {
    }

    public String send(String zapytanie) throws IOException {
        String odpowiedz;
        doSerwera.writeBytes(zapytanie + '\n');
        odpowiedz = odSerwera.readLine();
        //socket.close();
        return odpowiedz;
    }
}
