package com.unlucky.unlucky.server.socket;

import com.unlucky.unlucky.server.games.service.LotteryService;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ClientHandler implements Runnable {

    private final Socket clientSocket;
    private final LotteryService lotteryService;

    public ClientHandler(Socket socket, LotteryService lotteryService) {
        this.clientSocket = socket;
        this.lotteryService = lotteryService;
    }

    @Override
    public void run() {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
             PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)) {

            String line;
            while ((line = in.readLine()) != null) {
                String response = handleCommand(line);
                out.println(response);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                clientSocket.close();
            } catch (IOException ignored) {}
            System.out.println("Client disconnected.");
        }
    }

    public String handleCommand(String command) {
        String[] parts = command.split(" ");
        if (parts.length == 0) return "Error: No command provided";

        return switch (parts[0].toUpperCase()) {
            case "HELP" -> """
                    Commands:
                    CLASSIC PURCHASE <username> <quantity>
                    CLASSIC DRAW
                    CLASSIC TICKETS <username>
                    LOTTO649 PURCHASE <username> <n1 n2 n3 n4 n5 n6>
                    LOTTO649 DRAW
                    LOTTO649 TICKETS <username>
                    LOTTO649 CLAIM <username>
                    LOTTO649 NEWROUND
                    LOTTO649 ROUNDINFO""";
            case "CLASSIC" -> handleClassic(parts);
            case "LOTTO649" -> handleLotto649(parts);
            default -> "Error: Unknown command. Type HELP for help";
        };
    }

    public String handleClassic(String[] parts) {
        if (parts.length < 2) return "Error: Missing CLASSIC subcommand";

        switch (parts[1].toUpperCase()) {
            case "PURCHASE":
                if (parts.length != 4) return "Usage: CLASSIC PURCHASE <username> <quantity>";
                String user = parts[2];
                int quantity = Integer.parseInt(parts[3]);
                lotteryService.purchaseClassicTicket(user, quantity);
                return "purchased " + quantity + " tickets";

            case "DRAW":
                lotteryService.drawClassicLottery();
                return "drawing classic lottery";

            case "TICKETS":
                if (parts.length != 3) return "Usage: CLASSIC TICKETS <username>";
                return lotteryService.getUserClassicTickets(parts[2]).toString();

            default:
                return "Error: Unknown CLASSIC subcommand";
        }
    }

    public String handleLotto649(String[] parts) {
        if (parts.length < 2) return "Error: Missing LOTTO649 subcommand";

        switch (parts[1].toUpperCase()) {
            case "PURCHASE":
                if (parts.length != 9) return "Usage: LOTTO649 PURCHASE <username> <6 numbers>";
                String user = parts[2];
                List<Integer> numbers = new ArrayList<>();
                for (int i = 3; i < 9; i++) {
                    numbers.add(Integer.parseInt(parts[i]));
                }
                return lotteryService.purchaseLotto649Ticket(user, numbers).toString();

            case "DRAW":
                return lotteryService.drawLotto649().toString();

            case "TICKETS":
                if (parts.length != 3) return "Usage: LOTTO649 TICKETS <username>";
                return lotteryService.getUserLotto649Tickets(parts[2]).toString();

            case "CLAIM":
                if (parts.length != 3) return "Usage: LOTTO649 CLAIM <username>";
                return "Winnings: " + lotteryService.claimWinnings(parts[2]);

            case "NEWROUND":
                lotteryService.startNewLotto649Round();
                return "New Lotto649 round started.";

            case "ROUNDINFO":
                return lotteryService.getCurrentLotto649Round().toString();

            default:
                return "Error: Unknown LOTTO649 subcommand";
        }
    }
}