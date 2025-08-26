package com.unlucky.unlucky.server.socket;

import java.io.*;
import java.net.Socket;

public class ClientHandler implements Runnable {

    private final Socket clientSocket;

    public ClientHandler(Socket socket) {
        this.clientSocket = socket;
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

        switch (parts[0].toUpperCase()) {
            case "HELP":
                return "Commands: HELP <command>";

            default:
                return "Error: Unknown command. Type HELP for help";
        }
    }
}