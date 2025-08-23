package com.unlucky.unlucky.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class PerformanceClient {

    private final UserMethods userMethods = new UserMethods();
    private final ExecutorService executorService = Executors.newFixedThreadPool(100);

    public PerformanceClient() {
    }

    public void startClient() {
        for (int i = 0; i < 2000; i++) {
            String name = "name" + i;
            String mail = "mail" + i + "@mail.com";
            executorService.submit(() -> userMethods.registerUser(name, mail));
        }
        executorService.shutdown();
    }


    public static void main(String[] args) {
        new PerformanceClient().startClient();
    }
}
