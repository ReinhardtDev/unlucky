package com.unlucky.unlucky.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import com.unlucky.unlucky.logging.LoggingService;

import java.util.*;

public class LotteryMethods {

    private final LoggingService loggingService = new LoggingService();
    private final Connection connection = new Connection("http://localhost:8080");
    private final ObjectMapper mapper = new ObjectMapper();
    private final Random random = new Random();
    private final TCPConnection tcpConnection;

    public LotteryMethods(TCPConnection tcpConnection) {
        this.tcpConnection = tcpConnection;
    }

    public String purchaseClassicTicket(String username, int quantity) {
        try {
            long start = System.nanoTime();
            String command = "CLASSIC PURCHASE " + username + " " + quantity;
            String response = tcpConnection.sendMessage(command);

            long end = System.nanoTime();
            double elapsed = (double) (end - start) / 1000000;
            loggingService.log(LoggingService.ACTION.PURCHASE_TICKET_CLASSIC, elapsed, username, String.valueOf(quantity));

            return response;
        } catch (Exception e) {
            throw new RuntimeException("Failed to purchase ticket: " + e.getMessage());
        }
    }

    public String purchaseLotto649Ticket(String username, String numbers) {
        try {
            long start = System.nanoTime();
            String command = "LOTTO649 PURCHASE " + username + " " + numbers;
            tcpConnection.sendMessage(command);

            long end = System.nanoTime();
            double elapsed = (double) (end - start) / 1000000;
            loggingService.log(LoggingService.ACTION.PURCHASE_TICKET_649, elapsed, username, numbers);

            return "Purchased Lotto 6/49 ticket with numbers: " + numbers;
        } catch (Exception e) {
            throw new RuntimeException("Failed to purchase ticket: " + e.getMessage());
        }
    }

    public List<String> getUserClassicTickets(String username) {
        try {
            String response = connection.restGetRequest("/api/lottery/classic/tickets?username=" + username);

            List<Map<String, Object>> ticketsData = mapper.readValue(response, new TypeReference<List<Map<String, Object>>>() {});

            List<String> ticketStrings = new ArrayList<>();
            for (Map<String, Object> ticketData : ticketsData) {
                String ticketNumber = (String) ticketData.get("ticketNumber");
                Boolean isWinner = (Boolean) ticketData.get("winner");
                String purchaseDate = (String) ticketData.get("purchaseDate");

                String ticketString = "Ticket #" + ticketNumber +
                        " - Purchased: " + purchaseDate +
                        (isWinner != null && isWinner ? " - WINNER!" : "");
                ticketStrings.add(ticketString);
            }

            return ticketStrings;

        } catch (Exception e) {
            return List.of("No tickets available or server error: " + e.getMessage());
        }
    }

    public List<String> getUserClassicTicketHistory(String username) {
        try {
            String response = connection.restGetRequest("/api/lottery/classic/tickets/history?username=" + username);

            List<Map<String, Object>> ticketsData = mapper.readValue(response, new TypeReference<List<Map<String, Object>>>() {});

            List<String> ticketStrings = new ArrayList<>();
            for (Map<String, Object> ticketData : ticketsData) {
                String ticketNumber = (String) ticketData.get("ticketNumber");
                Boolean isWinner = (Boolean) ticketData.get("winner");
                String purchaseDate = (String) ticketData.get("purchaseDate");

                String ticketString = "Ticket #" + ticketNumber +
                        " - Purchased: " + purchaseDate +
                        (isWinner != null && isWinner ? " - WINNER!" : "");
                ticketStrings.add(ticketString);
            }

            return ticketStrings;

        } catch (Exception e) {
            return List.of("No history available or server error: " + e.getMessage());
        }
    }


    public List<String> getUserLotto649Tickets(String username) {
        try {
            String response = connection.restGetRequest("/api/lottery/lotto649/tickets?username=" + username);

            List<Map<String, Object>> ticketsData = mapper.readValue(response, new TypeReference<List<Map<String, Object>>>() {});

            List<String> ticketStrings = new ArrayList<>();
            for (Map<String, Object> ticketData : ticketsData) {
                List<Integer> numbers = (List<Integer>) ticketData.get("numbers");
                Integer matchingNumbers = (Integer) ticketData.get("matchingNumbers");
                Boolean isWinner = (Boolean) ticketData.get("winner");
                String purchaseDate = (String) ticketData.get("purchaseDate");

                String ticketString = "Numbers: " + numbers +
                        " - Matches: " + (matchingNumbers != null ? matchingNumbers : "0") +
                        " - Purchased: " + purchaseDate +
                        (isWinner != null && isWinner ? " - WINNER!" : "");
                ticketStrings.add(ticketString);
            }

            return ticketStrings;

        } catch (Exception e) {
            return List.of("No tickets available or server error: " + e.getMessage());
        }
    }

    public List<String> getUserLotto649TicketHistory(String username) {
        try {
            String response = connection.restGetRequest("/api/lottery/lotto649/tickets/history?username=" + username);

            List<Map<String, Object>> ticketsData = mapper.readValue(response, new TypeReference<List<Map<String, Object>>>() {});

            List<String> ticketStrings = new ArrayList<>();
            for (Map<String, Object> ticketData : ticketsData) {
                List<Integer> numbers = (List<Integer>) ticketData.get("numbers");
                Integer matchingNumbers = (Integer) ticketData.get("matchingNumbers");
                Boolean isWinner = (Boolean) ticketData.get("winner");
                String purchaseDate = (String) ticketData.get("purchaseDate");

                String ticketString = "Numbers: " + numbers +
                        " - Matches: " + (matchingNumbers != null ? matchingNumbers : "0") +
                        " - Purchased: " + purchaseDate +
                        (isWinner != null && isWinner ? " - WINNER!" : "");
                ticketStrings.add(ticketString);
            }

            return ticketStrings;

        } catch (Exception e) {
            // If parsing fails, return a helpful message
            return List.of("No tickets available or server error: " + e.getMessage());
        }
    }

    public void drawClassicLottery() {
        try {
            long start = System.nanoTime();

            String response = connection.restPostRequest("{}", "/api/lottery/classic/draw"); //connection.sendMessage()
            Map<String, Object> result = mapper.readValue(response, Map.class);

            System.out.println("🎉 CLASSIC LOTTERY DRAW COMPLETED!");
            System.out.println("Winner: " + result.get("winner"));
            System.out.println("Ticket: " + result.get("ticketNumber"));
            System.out.println("Prize: " + result.get("prize") + " currency");
            System.out.println("Total tickets: " + result.get("totalTickets"));

            long end = System.nanoTime();
            double elapsed = (double) (end - start) / 1000000;
            loggingService.log(LoggingService.ACTION.DRAW_CLASSIC, elapsed);

        } catch (Exception e) {
            throw new RuntimeException("Failed to draw classic lottery: " + e.getMessage());
        }
    }

    public Map<String, Object> drawLotto649() {
        try {
            long start = System.nanoTime();

            String response = connection.restPostRequest("{}", "/api/lottery/lotto649/draw"); //connection.sendMessage()
            Map<String, Object> result = mapper.readValue(response, Map.class);

            System.out.println("🎉 LOTTO 6/49 DRAW COMPLETED!");
            System.out.println("Winning numbers: " + result.get("winningNumbers"));

            List<Map<String, Object>> winners = (List<Map<String, Object>>) result.get("winners");
            if (winners == null || winners.isEmpty()) {
                System.out.println("No winners this round!");
            } else {
                System.out.println("Winners:");
                for (Map<String, Object> winner : winners) {
                    System.out.println("  " + winner.get("username") +
                            " - " + winner.get("matches") + " matches" +
                            " - Prize: " + winner.get("prize"));
                }
            }
            long end = System.nanoTime();
            double elapsed = (double) (end - start) / 1000000;
            loggingService.log(LoggingService.ACTION.DRAW_649, elapsed);

            return result;
        } catch (Exception e) {
            throw new RuntimeException("Failed to draw Lotto 6/49: " + e.getMessage());
        }
    }

    public int claimWinnings(String username) {
        try {
            String response = connection.restPostRequest("{}", "/api/lottery/lotto649/claim?username=" + username); //connection.sendMessage()
            return Integer.parseInt(response);
        } catch (Exception e) {
            throw new RuntimeException("Failed to claim winnings: " + e.getMessage());
        }
    }

    public void startNewLotto649Round() {
        try {
            connection.restPostRequest("{}", "/api/lottery/lotto649/new-round"); //connection.sendMessage()
            System.out.println("New Lotto 6/49 round started!");
        } catch (Exception e) {
            throw new RuntimeException("Failed to start new round: " + e.getMessage());
        }
    }

    public Map<String, Object> getCurrentLotto649Round() {
        try {
            String response = connection.restGetRequest("/api/lottery/lotto649/round-info"); //connection.sendMessage()
            return mapper.readValue(response, Map.class);
        } catch (Exception e) {
            throw new RuntimeException("Failed to get round info: " + e.getMessage());
        }
    }

    public List<Integer> generateRandomNumbers() {
        Set<Integer> numbers = new HashSet<>();
        while (numbers.size() < 6) {
            numbers.add(random.nextInt(49) + 1);
        }
        return new ArrayList<>(numbers).stream().sorted().toList();
    }
}