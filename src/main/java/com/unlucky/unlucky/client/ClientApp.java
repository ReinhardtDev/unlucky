package com.unlucky.unlucky.client;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class ClientApp {

    private final Scanner input = new Scanner(System.in);

    private String currentUser = null;

    public void startClient() {
        System.out.println("=== WELCOME TO UNLUCKY ===");
        while (true) {
            try {
                if (currentUser == null) {
                    loginScreen();
                } else {
                    homeMenu();
                }
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }

    private void loginScreen(){
        System.out.println("\n1) Login 2) Register 0) Exit");
        System.out.print("> ");
        String choice = input.nextLine().trim();
        switch (choice) {
            case "1" -> {
                System.out.println("===LOGIN===");
                System.out.println("Enter username: ");
                String username = input.nextLine().trim();

                String response = sendGetRequest("/api/users/" +  username);
                if (response == null) {
                    System.out.println("User does not exist");
                } else currentUser = username;
            }
            case "2" -> {
                System.out.println("===REGISTER===");
                System.out.println("Enter username: ");
                String username = input.nextLine().trim();
                System.out.println("Enter Email: ");
                String email = input.nextLine().trim();

                String json = String.format("{\"username\":\"%s\", \"email\":\"%s\"}", username, email);

                String response = sendPostRequest(json);

                System.out.println("Registered as " + username);
                currentUser = username;
            }
            case "0" -> {
                System.out.println("Goodbye!");
                System.exit(0);
            }
            default -> System.out.println("Invalid choice");
        }
    }

    private void homeMenu() {
        System.out.println("\n===UNLUCKY HOME MENU===");
        System.out.println("Logged in as " + currentUser);
        System.out.println("1) View Profile 2) Add Currency 3) Select Game 9) Logout 0) Exit");
        System.out.print("> ");
        String choice = input.nextLine().trim();

        switch (choice) {
            case "1" -> {
                System.out.println("Viewing profile");
                String response = sendGetRequest("/api/users/" + currentUser);
                System.out.println(response);
                homeMenu();
            }
            case "2" -> {
                System.out.println("Add currency");
            }
            case "3" -> {
                System.out.println("Select game");
            }
            case "9" -> currentUser = null;
            case "0" -> {
                System.out.println("Goodbye!");
                System.exit(0);
            }
            default -> System.out.println("Invalid choice");
        }

        System.exit(0);
    }

    private String sendGetRequest(String request) {
        try {
            URL url = new URL("http://localhost:8080" + request);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String inputLine;
            StringBuilder content = new StringBuilder();

            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }

            in.close();
            connection.disconnect();

            return content.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return "Error: " + e.getMessage();
        }
    }

    private String sendPostRequest(String jsonInput) {
        try {
            URL url = new URL("http://localhost:8080" + "/api/users/register");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json; utf-8");
            connection.setDoOutput(true);

            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = jsonInput.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = in.readLine()) != null) {
                response.append(line.trim());
            }
            in.close();
            connection.disconnect();

            return response.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return "Error: " + e.getMessage();
        }
    }

    public static void main(String[] args) {
        new ClientApp().startClient();
    }
}
