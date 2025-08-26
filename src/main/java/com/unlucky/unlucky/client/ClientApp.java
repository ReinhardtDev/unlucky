package com.unlucky.unlucky.client;

import com.fasterxml.jackson.core.JsonProcessingException;

import java.io.IOException;
import java.util.Scanner;

public class ClientApp {

    private final UserMethods userMethods = new UserMethods();
    private final Scanner input = new Scanner(System.in);
    private String currentUser = null;

    public void startClient() {
        System.out.println("___WELCOME_TO_UNLUCKY___");

        boolean running = true;
        while (running) {
            try {
                if (currentUser == null) {
                    running = loginScreen();
                } else if (currentUser.equals("admin")) {
                    running = adminMenu();
                } else {
                    running = homeMenu();
                }
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
                e.printStackTrace();
            }
        }

        System.out.println("Goodbye!");
    }

    private boolean loginScreen() throws JsonProcessingException {
        System.out.println("\n1) Login 2) Register 0) Exit");
        System.out.print("> ");
        String choice = input.nextLine().trim();

        switch (choice) {
            case "1" -> {
                System.out.println("___LOGIN___");
                System.out.print("Enter username: ");
                String userInput = input.nextLine().trim();
                if (userInput.equals("admin")) {
                    currentUser = "admin";
                } else {
                    String userName = userMethods.returnUserByName(userInput);
                    if (userName == null) {
                        System.out.println("User does not exist");
                    } else {
                        System.out.println("Logged in as " + userName);
                        currentUser = userName;
                    }
                }
            }
            case "2" -> {
                System.out.println("___REGISTER___");

                String username;
                while (true) {
                    System.out.println("Enter \"quit\" to quit");
                    System.out.print("Enter username: ");
                    username = input.nextLine().trim();

                    if(username.equalsIgnoreCase("quit")) return true;

                    if (username.length() < 3) {
                        System.out.println("Username must be at least 3 characters.");
                        continue;
                    }
                    if (username.equalsIgnoreCase("admin")) {
                        System.out.println("this is a reserved username.");
                        continue;
                    }
                    if (userMethods.returnUserByName(username) != null) {
                        System.out.println("Username already exists. Try another.");
                        continue;
                    }
                    break;
                }

                String email;
                while (true) {
                    System.out.print("Enter email: ");
                    email = input.nextLine().trim();

                    if(email.equalsIgnoreCase("quit")) return true;

                    if (!userMethods.isValidEmail(email)) {
                        System.out.println("Invalid email address. Try again.");
                        continue;
                    }
                    break;
                }

                userMethods.registerUser(username, email);
                System.out.println("Successfully registered!");
                currentUser = userMethods.returnUserByName(username);

            }
            case "0" -> {
                return false;
            }
            default -> System.out.println("Invalid choice");
        }
        return true;
    }

    private boolean adminMenu() throws IOException {
        System.out.println("___ADMIN___");
        System.out.println("1) View a user profile 2) View all users 9) Logout 0) Quit");
        System.out.print("> ");
        String choice = input.nextLine().trim();

        switch (choice) {
            case "1" -> {
                System.out.print("Enter a username: ");
                String username = input.nextLine().trim();
                if(userMethods.returnUserByName(username) != null) {
                    userMethods.displayUserProfile(username);
                } else System.out.println("User does not exist.");

            }
            case "2" -> {
                System.out.println("View all users (not implemented)");
            }
            case "3" -> {
                userMethods.testTCPConnection();
            }
            case "9" -> {
                System.out.println("Logged out.");
                currentUser = null;
            }
            case "0" -> {
                return false;
            }
            default -> System.out.println("Invalid choice");
        }
        return true;
    }

    private boolean homeMenu() throws JsonProcessingException {
        System.out.println("\n___UNLUCKY_HOME_MENU___");
        System.out.println("Logged in as " + currentUser);
        System.out.println("1) View Profile 2) Add Currency 3) Select Game 9) Logout 0) Exit");
        System.out.print("> ");
        String choice = input.nextLine().trim();

        switch (choice) {
            case "1" -> {
                System.out.println("Viewing profile...");
                userMethods.displayUserProfile(currentUser);
            }
            case "2" -> {
                System.out.print("Enter amount of currency you would like to add: ");
                if (input.hasNextInt()) {
                    int amount = input.nextInt();
                    input.nextLine();
                    System.out.print("Enter Credit Card information: ");
                    String creditCard = input.nextLine().trim();
                    userMethods.addCurrency(currentUser, amount);
                    System.out.println("Successfully added " + amount + " currency to your account");
                } else {
                    System.out.println("Invalid amount");
                    input.nextLine();
                }
            }
            case "3" -> {
                System.out.println("Select game (not implemented)");
            }
            case "9" -> {
                System.out.println("Logged out.");
                currentUser = null;
            }
            case "0" -> {
                return false;
            }
            default -> System.out.println("Invalid choice");
        }
        return true;
    }

    public static void main(String[] args) {
        new ClientApp().startClient();
    }
}
