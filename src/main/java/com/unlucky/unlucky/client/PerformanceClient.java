package com.unlucky.unlucky.client;

import com.unlucky.unlucky.logging.LoggingService;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class PerformanceClient {

    private final LoggingService loggingService = new LoggingService();
    private final UserMethods userMethods = new UserMethods();
    private final ExecutorService executorService = Executors.newFixedThreadPool(20);

    public PerformanceClient() {
    }

    public void startClient() {

        //Submit 2000 client requests
        for (int i = 0; i < 2000; i++) {
            String name = "name" + i;
            String mail = "mail" + i + "@mail.com";
            executorService.submit(() -> userMethods.registerUser(name, mail));
        }

        //Execute them at once with 20 threads and measure execution time
        long start = System.nanoTime();
        executorService.shutdown();
        while (!executorService.isTerminated()) {}
        long end = System.nanoTime();
        //Divide by 1 mil for ms, then divide by 2000 for average -> divide by 2 bil
        double elapsed = (double) (end - start) / 2000000000 * 20;
        loggingService.log(LoggingService.ACTION.AVERAGE_TIME, elapsed);



    }


    public static void main(String[] args) {
        new PerformanceClient().startClient();
    }
}
