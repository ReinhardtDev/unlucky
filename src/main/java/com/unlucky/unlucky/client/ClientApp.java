package com.unlucky.unlucky.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import java.io.IOException;
import java.util.*;

public class ClientApp {
    private final UserMethods userMethods = new UserMethods();
    private final Scanner input = new Scanner(System.in);
    private String currentUser = null;
    private final TCPConnection tcpConnection = new TCPConnection();
    private final LotteryMethods lotteryMethods = new LotteryMethods(tcpConnection);


    public void startClient(String ip, int port) {
        if(!tcpConnection.startConnection(ip, port)) {
            return;
        }
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
            }
        }

        tcpConnection.stopConnection();
        System.out.println("Goodbye!");
    }

    private boolean loginScreen() throws JsonProcessingException {
        System.out.println("1) Login \n2) Register \n0) Exit");
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
                    if (userName == null || userName.isEmpty()) {
                        System.out.println("User does not exist.");
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
                    if (username.equalsIgnoreCase("quit")) return true;
                    if (username.length() < 3) {
                        System.out.println("Username must be at least 3 characters.");
                        continue;
                    }
                    if (username.equalsIgnoreCase("admin")) {
                        System.out.println("This is a reserved username.");
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
                    if (email.equalsIgnoreCase("quit")) return true;
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
            case "0" -> { return false; }
            default -> System.out.println("Invalid choice");
        }
        return true;
    }

    private boolean adminMenu() throws IOException {
        System.out.println("___ADMIN___");
        System.out.println("1) View a user profile \n2) View all users \n3) Draw Classic Lottery \n4) Lotto 649 Management \n9) Logout \n0) Quit");
        System.out.print("> ");
        String choice = input.nextLine().trim();

        switch (choice) {
            case "1" -> {
                System.out.print("Enter a username: ");
                String username = input.nextLine().trim();
                if (userMethods.returnUserByName(username) != null) {
                    userMethods.displayUserProfile(username);
                } else System.out.println("User does not exist.");
            }
            case "2" -> System.out.println("View all users functionality would be implemented here");
            case "3" -> {
                System.out.println("🎰 Drawing Classic Lottery...");
                lotteryMethods.drawClassicLottery();
                // The draw method handles all output including winner announcement
            }

            case "4" -> lotto649AdminMenu();

            case "5" -> {
                System.out.println("Enter Custom TCP command: ");
                String command = input.nextLine().trim();
                System.out.println(tcpConnection.sendMessage(command));
            }

            case "9" -> {
                System.out.println("Logged out.");
                currentUser = null;
            }
            case "0" -> { return false; }
            default -> System.out.println("Invalid choice");
        }
        return true;
    }

    private void lotto649AdminMenu() {
        System.out.println("___LOTTO_649_ADMIN___");
        System.out.println("1) Draw Lotto 649 \n2) Start New Round \n3) View Round Info \n4) Back");
        System.out.print("> ");
        String choice = input.nextLine().trim();

        switch (choice) {
            case "1" -> {
                System.out.println("🎰 Drawing Lotto 6/49...");
                lotteryMethods.drawLotto649();
            }
            case "2" -> {
                System.out.print("Are you sure you want to start a new round? All existing tickets will be cleared. (y/n): ");
                String confirm = input.nextLine().trim().toLowerCase();
                if (confirm.equals("y") || confirm.equals("yes")) {
                    lotteryMethods.startNewLotto649Round();
                } else {
                    System.out.println("Operation cancelled.");
                }
            }
            case "3" -> {
                try {
                    Map<String, Object> roundInfo = lotteryMethods.getCurrentLotto649Round();
                    System.out.println("=== CURRENT LOTTO 6/49 ROUND INFO ===");
                    System.out.println("Tickets sold: " + roundInfo.get("ticketsSold"));
                    System.out.println("Last draw: " + roundInfo.get("lastDraw"));
                } catch (Exception e) {
                    System.out.println("Error getting round info: " + e.getMessage());
                }
            }
            default -> System.out.println("Invalid choice");
        }
    }

    private boolean homeMenu() throws JsonProcessingException {
        System.out.println("\n___UNLUCKY_HOME_MENU___");
        System.out.println("Logged in as " + currentUser);
        System.out.println("1) View Profile \n2) Add Currency \n3) Classic Lottery \n4) Lotto 649 \n5) Claim Winnings \n9) Logout \n0) Exit");
        System.out.print("> ");
        String choice = input.nextLine().trim();

        switch (choice) {
            case "1" -> userMethods.displayUserProfile(currentUser);
            case "2" -> {
                System.out.print("Enter amount of currency to add: ");
                if (input.hasNextInt()) {
                    int amount = input.nextInt();
                    input.nextLine();
                    System.out.print("Enter Credit Card information: ");
                    String creditCard = input.nextLine().trim(); // placeholder
                    userMethods.addCurrency(currentUser, amount);
                    System.out.println("Added " + amount + " currency to your account");
                } else {
                    System.out.println("Invalid amount");
                    input.nextLine();
                }
            }
            case "3" -> classicLotteryMenu();
            case "4" -> lotto649Menu();
            case "5" -> {
                System.out.println("💰 Claiming Winnings...");
                try {
                    int winnings = lotteryMethods.claimWinnings(currentUser);
                    if (winnings > 0) {
                        System.out.println("Successfully claimed " + winnings + " currency!");
                    } else {
                        System.out.println("No winnings to claim at this time.");
                    }
                } catch (Exception e) {
                    System.out.println("Error claiming winnings: " + e.getMessage());
                }
            }
            case "9" -> {
                currentUser = null;
                System.out.println("Logged out.");
            }
            case "0" -> { return false; }
            default -> System.out.println("Invalid choice");
        }
        return true;
    }

    private void classicLotteryMenu() {
        System.out.println("___CLASSIC_LOTTERY___");
        System.out.println("1) Buy Tickets \n2) View My Active Tickets \n3) View Ticket History");
        System.out.print("> ");
        String choice = input.nextLine().trim();

        switch (choice) {
            case "1" -> {
                System.out.print("How many tickets? (1 ticket = $2): ");
                if (input.hasNextInt()) {
                    int quantity = input.nextInt();
                    input.nextLine();
                    try {
                        String result = lotteryMethods.purchaseClassicTicket(currentUser, quantity);
                        System.out.println(result);
                    } catch (Exception e) {
                        System.out.println("Error: " + e.getMessage());
                    }
                } else {
                    System.out.println("Invalid quantity");
                    input.nextLine();
                }
            }
            case "2" -> {
                try {
                    List<String> tickets = lotteryMethods.getUserClassicTickets(currentUser);
                    if (tickets.isEmpty()) {
                        System.out.println("No tickets purchased yet");
                    } else {
                        System.out.println("Your Classic Lottery Tickets:");
                        for (int i = 0; i < tickets.size(); i++) {
                            System.out.println((i + 1) + ") " + tickets.get(i));
                        }
                    }
                } catch (Exception e) {
                    System.out.println("Error: " + e.getMessage());
                }
            }
            case "3" -> {
                try {
                    List<String> tickets = lotteryMethods.getUserClassicTicketHistory(currentUser);
                    if (tickets.isEmpty()) {
                        System.out.println("No ticket history yet");
                    } else {
                        System.out.println("Your Classic Lottery Ticket History:");
                        for (int i = 0; i < tickets.size(); i++) {
                            System.out.println((i + 1) + ") " + tickets.get(i));
                        }
                    }
                } catch (Exception e) {
                    System.out.println("Error: " + e.getMessage());
                }
            }
            default -> System.out.println("Invalid choice");
        }
    }

    private void lotto649Menu() {
        System.out.println("___LOTTO_6/49___");
        System.out.println("1) Buy Ticket \n2) View My Tickets \n3) Quick Pick (Random Numbers) \n4) View My Ticket History");
        System.out.print("> ");
        String choice = input.nextLine().trim();

        switch (choice) {
            case "1" -> {
                System.out.println("Enter 6 unique numbers (1-49) separated by spaces:");
                try {
                    String numbersInput = input.nextLine().trim();
                    List<Integer> numbers = new ArrayList<>();
                    Scanner numberScanner = new Scanner(numbersInput);
                    while (numberScanner.hasNextInt()) {
                        int num = numberScanner.nextInt();
                        if (num < 1 || num > 49) {
                            System.out.println("Numbers must be between 1 and 49");
                            numberScanner.close();
                            return;
                        }
                        numbers.add(num);
                    }
                    numberScanner.close();

                    if (numbers.size() != 6) {
                        System.out.println("Please enter exactly 6 numbers");
                        return;
                    }

                    if (new HashSet<>(numbers).size() != 6) {
                        System.out.println("All numbers must be unique");
                        return;
                    }

                    String result = lotteryMethods.purchaseLotto649Ticket(currentUser, numbersInput);
                    System.out.println(result);
                } catch (Exception e) {
                    System.out.println("Error: " + e.getMessage());
                }
            }
            case "2" -> {
                try {
                    List<String> tickets = lotteryMethods.getUserLotto649Tickets(currentUser);
                    if (tickets.isEmpty()) {
                        System.out.println("No tickets purchased yet");
                    } else {
                        System.out.println("Your Lotto 6/49 Tickets:");
                        for (int i = 0; i < tickets.size(); i++) {
                            System.out.println((i + 1) + ") " + tickets.get(i));
                        }
                    }
                } catch (Exception e) {
                    System.out.println("Error: " + e.getMessage());
                }
            }
            case "3" -> {
                try {
                    // Generate random numbers for quick pick
                    List<Integer> randomNumbers = lotteryMethods.generateRandomNumbers();
                    System.out.println("Your quick pick numbers: " + randomNumbers);

                    String result = lotteryMethods.purchaseLotto649Ticket(currentUser, randomNumbers.toString());
                    System.out.println(result);
                } catch (Exception e) {
                    System.out.println("Error: " + e.getMessage());
                }
            }
            case "4" -> {
                try {
                    List<String> tickets = lotteryMethods.getUserLotto649TicketHistory(currentUser);
                    if (tickets.isEmpty()) {
                        System.out.println("No ticket history yet");
                    } else {
                        System.out.println("Your Classic Lottery Ticket History:");
                        for (int i = 0; i < tickets.size(); i++) {
                            System.out.println((i + 1) + ") " + tickets.get(i));
                        }
                    }
                } catch (Exception e) {
                    System.out.println("Error: " + e.getMessage());
                }
            }
            default -> System.out.println("Invalid choice");
        }
    }

    public static void main(String[] args) throws IOException {
        new ClientApp().startClient("localhost", 5000);
    }
}