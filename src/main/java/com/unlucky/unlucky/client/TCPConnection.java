package com.unlucky.unlucky.client;

import java.io.*;
import java.net.Socket;

public class TCPConnection {

    private Socket clientSocket;
    private PrintWriter out;
    private BufferedReader in;

    public boolean startConnection(String ip, int port) {
        try {
            clientSocket = new Socket(ip, port);
            out = new PrintWriter(clientSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            System.out.println("Connected to server " + ip + ":" + port);
            return true;
        } catch (IOException e) {
            System.out.println("Could not connect to server " + ip + ":" + port);
            return false;
        }
    }

    public String sendMessage(String message) {
        try {
            out.println(message);
            return in.readLine();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void stopConnection() {
        try {
            in.close();
            out.close();
            clientSocket.close();
            System.out.println("Disconnected from server");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
