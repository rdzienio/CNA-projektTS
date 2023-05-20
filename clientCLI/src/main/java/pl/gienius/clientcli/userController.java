package pl.gienius.clientcli;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.Scanner;

@Controller
public class userController {

    private final ApiService apiService = new ApiService();

    private String currentUser = "";

    private static final Logger log = LoggerFactory.getLogger(userController.class);

    public userController() throws IOException {
    }

    void welcome() {
        System.out.println("Welcome to SilkRoad2.1v3.7!");
    }

    private void showMainMenu() {
        welcome();
        System.out.println("-----------------------");
        System.out.println("1) Register");
        System.out.println("2) Log in");
        System.out.println("3) Check last 10 posts");
        System.out.println("4) Send post");
        System.out.println("5) File transfer");
        if (!currentUser.isBlank())
            System.out.println("9) Logout");
        System.out.println("0) Exit");
    }

    private void showRegisterMenu() {
        System.out.println("New here?");
        System.out.println("-------------------------------");
        System.out.println("1) Create new account");
        System.out.println("2) Already got account? Log in!");
        System.out.println("0) Back to main menu");
    }

    private void showLoginMenu() {
        System.out.println("Log in");
        System.out.println("-------------------------------");
        System.out.println("1) Login");
        System.out.println("0) Back to main menu");
    }

    private void showFileMenu() {
        System.out.println("File transfer");
        System.out.println("-------------------------------");
        System.out.println("1) Upload file");
        System.out.println("2) Download file");
        System.out.println("0) Back to main menu");
    }

    public void start() {
        Scanner scanner = new Scanner(System.in);
        int userNo = 0;
        while (true) {
            showMainMenu();
            userNo = scanner.nextInt();
            switch (userNo) {
                case 1:
                    registerMenu();
                    break;
                case 2:
                    loginMenu();
                    break;
                case 3:
                    getPosts();
                    break;
                case 4:
                    sendPost();
                    break;
                case 5:
                    fileMenu();
                    break;
                case 9:
                    if (!currentUser.isEmpty()) {
                        currentUser = "";
                    }
                    break;
                case 0:
                    if (!currentUser.isEmpty()) {
                        currentUser = "";
                    }
                    System.out.println("Bye <3");
                    return;
                default:
                    System.out.println("Try again");
                    break;
            }
        }
    }

    void registerMenu() {
        Scanner scanner = new Scanner(System.in);
        String response;
        int choose = 0;
        while (true) {
            showRegisterMenu();
            choose = scanner.nextInt();
            switch (choose) {
                case 1:
                    response = createUserMenu();
                    if (response.isBlank()) System.out.println("Register error, try again!");
                    else System.out.println(response);
                    return;
                case 2:
                    response = login();
                    if (response.isBlank()) System.out.println("Login error, try again!");
                    else System.out.println(response);
                    return;
                case 0:
                    return;
                default:
                    System.out.println("Try again");
                    break;
            }
        }
    }

    private String createUserMenu() {
        Scanner scanner = new Scanner(System.in);
        String response = "";
        String name;
        String passwd;
        System.out.println("Create account");
        System.out.println("-------------------------------");
        System.out.println("Enter username: ");
        name = scanner.nextLine();
        System.out.println("Enter password: ");
        passwd = scanner.nextLine();
        try {
            response = apiService.send("register_request;" + name + ";" + passwd + ";");
            log.info("Register response: " + response);
        } catch (IOException ioException) {
            log.error("Register error: " + ioException.getMessage());
        }
        return response;
    }

    private String login() {
        Scanner scanner = new Scanner(System.in);
        String name;
        String passwd;
        String response = "";
        System.out.println("Log in");
        System.out.println("-------------------------------");
        System.out.println("Enter username: ");
        name = scanner.nextLine();
        System.out.println("Enter password: ");
        passwd = scanner.nextLine();
        try {
            response = apiService.send("login_request;" + name + ";" + passwd + ";");
            String[] responseParts = response.split(";", 2);
            if (responseParts[0].equals("200")) currentUser = name;
            log.info("Login response: " + response);
        } catch (IOException ioException) {
            log.error("Login error: " + ioException.getMessage());
        }
        return response;
    }

    private void loginMenu() {
        Scanner scanner = new Scanner(System.in);
        String response;
        int choose = 0;
        while (true) {
            showLoginMenu();
            choose = scanner.nextInt();
            switch (choose) {
                case 1:
                    response = login();
                    if (response.isBlank()) System.out.println("Login error, try again!");
                    else System.out.println(response);
                    break;
                case 0:
                    return;
                default:
                    System.out.println("Try again");
                    break;
            }
        }
    }

    private void getPosts() {
        if (currentUser.isBlank()) {
            log.warn("You must log in first!");
            return;
        }
        String response = "";
        try {
            response = apiService.send("post_get;" + currentUser);
            log.info("Get posts" +
                    " response: " + response);
        } catch (IOException ioException) {
            log.error("Get posts error: " + ioException.getMessage());
        }
        String[] responseParts = response.split(";");
        if (responseParts[0].equals("200")) {
            String[] posts = responseParts[1].split("\t%\t");
            for (String post : posts) {
                System.out.println(post);
            }
            System.out.println();
        } else
            System.out.println(response);
    }

    private void sendPost() {
        if (currentUser.isBlank()) {
            log.warn("You must log in first!");
            return;
        }
        Scanner scanner = new Scanner(System.in);
        String post;
        String response = "";
        System.out.println("Your post: ");
        post = scanner.nextLine();
        try {
            response = apiService.send("post_put;" + currentUser + ";" + post + ";");
            log.info("Send post response: " + response);
        } catch (IOException ioException) {
            log.error("Send post error: " + ioException.getMessage());
        }
    }

    private void fileMenu() {
        Scanner scanner = new Scanner(System.in);
        int choose = 0;
        while (true) {
            showFileMenu();
            choose = scanner.nextInt();
            switch (choose) {
                case 1:
                    uploadFile();
                    break;
                case 2:
                    downloadFile();
                    break;
                case 0:
                    return;
                default:
                    System.out.println("Try again");
                    break;
            }
        }
    }

    private void uploadFile() {
        if (currentUser.isBlank()) {
            log.warn("You must log in first!");
            return;
        }
        String response = "";
        String filePath;
        String fileName;
        Scanner scanner = new Scanner(System.in);
        System.out.println("Type in file path:");
        filePath = scanner.nextLine();
        if (filePath.isBlank()) {
            log.warn("Incorrect upload file path.");
            return;
        }
        System.out.println("Save file as:");
        fileName = scanner.nextLine();
        if (fileName.isBlank()) {
            log.warn("Incorrect upload file name.");
            return;
        }
        try {
            byte[] fileBytes = Files.readAllBytes(Paths.get(filePath));
            String encodedFile = Base64.getEncoder().encodeToString(fileBytes);
            try {
                response = apiService.send("upload_request;" + currentUser + ";" + fileName + ";" + encodedFile);
                log.info("Upload file" +
                        " response: " + response);
            } catch (IOException ioException) {
                log.error("Upload file error: " + ioException.getMessage());
            }
        } catch (IOException e) {
            log.error("File reading error: " + e.getMessage());
        }
    }

    ;

    private void downloadFile() {
        if (currentUser.isBlank()) {
            log.warn("You must log in first!");
            return;
        }
        String response = "";
        String fileName;
        Scanner scanner = new Scanner(System.in);
        System.out.println("File to download:");
        fileName = scanner.nextLine();
        if (fileName.isBlank()) {
            log.warn("Incorrect download file name.");
            return;
        }
        try {
            response = apiService.send("download_request;" + currentUser + ";" + fileName);
            log.info("Download file" +
                    " response: " + response);
        } catch (IOException ioException) {
            log.error("Download file error: " + ioException.getMessage());
        }
        String[] responseParts = response.split(";", 2);
        if ("success".equals(responseParts[0])) {
            String encodedFile = responseParts[1];
            byte[] fileBytes = Base64.getDecoder().decode(encodedFile);
            String destinationPath = System.getProperty("user.home") + File.separator + "KOPIA_" + fileName;
            try {
                Files.write(Paths.get(destinationPath), fileBytes);
                log.info("200;File downloaded successfully: " + destinationPath);

            } catch (IOException e) {
                System.err.println("File writing ERROR: " + e.getMessage());
                log.error("File ERROR");
            }
        } else {
            log.info(response);
        }

    }

    ;

}
