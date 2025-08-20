package com.unlucky.unlucky.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Scanner;

public class ClientApp {

    private final Connection connection = new Connection("http://localhost:8080");
    private final ObjectMapper mapper = new ObjectMapper();
    private final UserMethods userMethods = new UserMethods();
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

    private void loginScreen() throws JsonProcessingException {
        System.out.println("\n1) Login 2) Register 0) Exit");
        System.out.print("> ");
        String choice = input.nextLine().trim();
        switch (choice) {
            case "1" -> {
                System.out.println("===LOGIN===");
                System.out.println("Enter username: ");
                String userInput = input.nextLine().trim();
                String userName = userMethods.returnUserByName(userInput);
                if (userName == null) {
                    System.out.println("User does not exist");
                } else {
                    System.out.println("Logged in as " + userName);
                    currentUser = userName;
                }
            }
            case "2" -> {
                System.out.println("===REGISTER===");
                System.out.println("Enter username: ");
                String username = input.nextLine().trim();
                System.out.println("Enter Email: ");
                String email = input.nextLine().trim();

                userMethods.registerUser(username, email);

                System.out.println("successfully registered");
            }
            case "0" -> {
                System.out.println("Goodbye!");
                System.exit(0);
            }
            default -> System.out.println("Invalid choice");
        }
    }

    private void homeMenu() throws JsonProcessingException {
        System.out.println("\n===UNLUCKY HOME MENU===");
        System.out.println("Logged in as " + currentUser);
        System.out.println("1) View Profile 2) Add Currency 3) Select Game 9) Logout 0) Exit");
        System.out.print("> ");
        String choice = input.nextLine().trim();

        switch (choice) {
            case "1" -> {
                System.out.println("Viewing profile");
                userMethods.displayUserProfile(currentUser);
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

    public static void main(String[] args) {
        new ClientApp().startClient();
    }
}
