package com.unlucky.unlucky.server.socket;

import com.unlucky.unlucky.server.games.service.LotteryService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
public class SocketServer implements CommandLineRunner {

    private static final int PORT = 5000;
    private final ExecutorService pool = Executors.newCachedThreadPool();
    private final LotteryService lotteryService;

    public SocketServer(LotteryService lotteryService) {
        this.lotteryService = lotteryService;
    }

    @Override
    public void run(String... args) {
        new Thread(() -> {
            try (ServerSocket serverSocket = new ServerSocket(PORT)) {
                System.out.println("Socket server running on port " + PORT);

                while (true) {
                    Socket clientSocket = serverSocket.accept();
                    System.out.println("Client connected: " + clientSocket.getInetAddress());
                    pool.submit(new ClientHandler(clientSocket, lotteryService));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }
}