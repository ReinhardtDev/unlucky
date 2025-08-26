package com.unlucky.unlucky.client;

import java.util.*;

public class LotteryMethods {

    private final UserMethods userMethods;

    private final Map<String, List<ClassicTicket>> classicTickets = new HashMap<>();
    private final Map<String, List<Lotto649Ticket>> lotto649Tickets = new HashMap<>();
    private final Random random = new Random();

    public LotteryMethods() {
        this.userMethods = new UserMethods();
    }

    public String purchaseClassicTicket(String username, int quantity) {
        try {
            int cost = quantity * 2;
            int currentBalance = userMethods.getBalance(username);

            if (currentBalance < cost) {
                throw new RuntimeException("Insufficient balance. Need: " + cost + ", Have: " + currentBalance);
            }

            userMethods.addCurrency(username, -cost);

            List<ClassicTicket> userTickets = classicTickets.getOrDefault(username, new ArrayList<>());

            for (int i = 0; i < quantity; i++) {
                String ticketNumber = generateTicketNumber();
                userTickets.add(new ClassicTicket(ticketNumber, false));
            }

            classicTickets.put(username, userTickets);
            return "Purchased " + quantity + " classic lottery tickets for " + cost + " currency";

        } catch (Exception e) {
            throw new RuntimeException("Failed to purchase ticket: " + e.getMessage());
        }
    }

    public String purchaseLotto649Ticket(String username, List<Integer> numbers) {
        try {
            if (numbers.size() != 6) {
                throw new RuntimeException("Must provide exactly 6 numbers");
            }

            if (numbers.stream().distinct().count() != 6) {
                throw new RuntimeException("All numbers must be unique");
            }

            for (Integer number : numbers) {
                if (number < 1 || number > 49) {
                    throw new RuntimeException("Numbers must be between 1 and 49");
                }
            }

            int cost = 5;
            int currentBalance = userMethods.getBalance(username);

            if (currentBalance < cost) {
                throw new RuntimeException("Insufficient balance. Need: " + cost + ", Have: " + currentBalance);
            }

            userMethods.addCurrency(username, -cost);
            List<Lotto649Ticket> userTickets = lotto649Tickets.getOrDefault(username, new ArrayList<>());

            String ticketId = "L649-" + System.currentTimeMillis() + "-" + random.nextInt(1000);
            userTickets.add(new Lotto649Ticket(ticketId, numbers, 0, false));

            lotto649Tickets.put(username, userTickets);
            return "Purchased Lotto 6/49 ticket with numbers: " + numbers + " for " + cost + " currency";

        } catch (Exception e) {
            throw new RuntimeException("Failed to purchase ticket: " + e.getMessage());
        }
    }

    public List<String> getUserClassicTickets(String username) {
        try {
            List<ClassicTicket> tickets = classicTickets.getOrDefault(username, new ArrayList<>());
            return tickets.stream()
                    .map(ticket -> "Ticket #" + ticket.number +
                            (ticket.isWinner ? " - WINNER!" : ""))
                    .toList();
        } catch (Exception e) {
            throw new RuntimeException("Failed to get tickets: " + e.getMessage());
        }
    }

    public List<String> getUserLotto649Tickets(String username) {
        try {
            List<Lotto649Ticket> tickets = lotto649Tickets.getOrDefault(username, new ArrayList<>());
            return tickets.stream()
                    .map(ticket -> "Numbers: " + ticket.numbers +
                            " - Matches: " + ticket.matchingNumbers +
                            (ticket.isWinner ? " - WINNER!" : ""))
                    .toList();
        } catch (Exception e) {
            throw new RuntimeException("Failed to get tickets: " + e.getMessage());
        }
    }

    public void drawClassicLottery() {
        try {
            drawClassicLotteryInMemory();
        } catch (Exception e) {
            throw new RuntimeException("Failed to draw classic lottery: " + e.getMessage());
        }
    }

    private void drawClassicLotteryInMemory() {
        List<ClassicTicket> allTickets = new ArrayList<>();
        for (List<ClassicTicket> userTickets : classicTickets.values()) {
            allTickets.addAll(userTickets);
        }

        if (allTickets.isEmpty()) {
            System.out.println("No tickets to draw!");
            return;
        }

        ClassicTicket winner = allTickets.get(random.nextInt(allTickets.size()));
        winner.isWinner = true;

        String winningUser = null;
        for (Map.Entry<String, List<ClassicTicket>> entry : classicTickets.entrySet()) {
            if (entry.getValue().contains(winner)) {
                winningUser = entry.getKey();
                break;
            }
        }

        if (winningUser != null) {
            int prize = (int) (allTickets.size() * 1.5); // 75% of total sales
            userMethods.addCurrency(winningUser, prize);
            System.out.println("🎉 WINNER: " + winningUser + " with ticket: " + winner.number);
            System.out.println("💰 Prize: " + prize + " currency");
        }

        classicTickets.clear();
        System.out.println("All classic lottery tickets have been cleared after the draw.");
    }

    public Map<String, Object> drawLotto649() {
        try {
            return drawLotto649InMemory();
        } catch (Exception e) {
            throw new RuntimeException("Failed to draw Lotto 6/49: " + e.getMessage());
        }
    }

    private Map<String, Object> drawLotto649InMemory() {
        List<Integer> winningNumbers = generateWinningNumbers();
        System.out.println("Winning numbers: " + winningNumbers);

        Map<String, Object> result = new HashMap<>();
        result.put("winningNumbers", winningNumbers);
        List<String> winners = new ArrayList<>();

        for (Map.Entry<String, List<Lotto649Ticket>> entry : lotto649Tickets.entrySet()) {
            String username = entry.getKey();
            List<Lotto649Ticket> tickets = entry.getValue();

            for (Lotto649Ticket ticket : tickets) {
                int matches = countMatches(ticket.numbers, winningNumbers);
                ticket.matchingNumbers = matches;
                ticket.isWinner = matches >= 4;

                if (matches >= 4) {
                    int prize = switch (matches) {
                        case 4 -> 100;
                        case 5 -> 1000;
                        case 6 -> 100000;
                        default -> 0;
                    };
                    userMethods.addCurrency(username, prize);
                    winners.add(username + " matched " + matches + " numbers - Prize: " + prize);
                    System.out.println("Winner: " + username + " - " + matches + " matches - Prize: " + prize);
                }
            }
        }

        if (winners.isEmpty()) {
            System.out.println("No winners this round!");
        }

        result.put("winners", winners);

        lotto649Tickets.clear();
        System.out.println("All Lotto 6/49 tickets have been cleared after the draw.");

        return result;
    }

    public int claimWinnings(String username) {
        try {
            System.out.println("Winnings are automatically distributed during the draw.");
            return 0;
        } catch (Exception e) {
            throw new RuntimeException("Failed to claim winnings: " + e.getMessage());
        }
    }

    public void startNewLotto649Round() {
        try {
            lotto649Tickets.clear();
            System.out.println("New Lotto 6/49 round started! All previous tickets have been cleared.");
        } catch (Exception e) {
            throw new RuntimeException("Failed to start new round: " + e.getMessage());
        }
    }

    public Map<String, Object> getCurrentLotto649Round() {
        try {
            Map<String, Object> roundInfo = new HashMap<>();
            roundInfo.put("ticketsSold", lotto649Tickets.values().stream().mapToInt(List::size).sum());
            roundInfo.put("lastDraw", new Date());
            return roundInfo;
        } catch (Exception e) {
            throw new RuntimeException("Failed to get round info: " + e.getMessage());
        }
    }

    private String generateTicketNumber() {
        return String.format("%08d", random.nextInt(100000000));
    }

    private List<Integer> generateWinningNumbers() {
        Set<Integer> numbers = new HashSet<>();
        while (numbers.size() < 6) {
            numbers.add(random.nextInt(49) + 1);
        }
        return new ArrayList<>(numbers).stream().sorted().toList();
    }

    private int countMatches(List<Integer> ticketNumbers, List<Integer> winningNumbers) {
        int matches = 0;
        for (Integer number : ticketNumbers) {
            if (winningNumbers.contains(number)) {
                matches++;
            }
        }
        return matches;
    }

    private static class ClassicTicket {
        String number;
        boolean isWinner;

        ClassicTicket(String number, boolean isWinner) {
            this.number = number;
            this.isWinner = isWinner;
        }
    }

    private static class Lotto649Ticket {
        String id;
        List<Integer> numbers;
        int matchingNumbers;
        boolean isWinner;

        Lotto649Ticket(String id, List<Integer> numbers, int matchingNumbers, boolean isWinner) {
            this.id = id;
            this.numbers = numbers;
            this.matchingNumbers = matchingNumbers;
            this.isWinner = isWinner;
        }
    }
}