package com.unlucky.unlucky.client;

import com.unlucky.unlucky.logging.LoggingService;
import com.unlucky.unlucky.server.user.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class PerformanceClient {

    private final LotteryMethods lotteryMethods = new LotteryMethods();
    private final LoggingService loggingService = new LoggingService();
    private final UserMethods userMethods = new UserMethods();;

    public PerformanceClient() {
    }

    public void startClient() {
        int threads = 10;
        ExecutorService executorService = Executors.newFixedThreadPool(threads);

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
        //Multiply by 20 to simulate realistic speedup by multi-threading
        double elapsed = (double) (end - start) / 1000000;
        double averageElapsed = (double) (end - start) / 2000000000 * threads;
        loggingService.log(LoggingService.ACTION.TOTAL_TIME, elapsed);
        loggingService.log(LoggingService.ACTION.AVERAGE_TIME, averageElapsed);
        loggingService.logStage("Stage 1 complete", true);


        executorService = Executors.newFixedThreadPool(threads);
        //Get all users
        ArrayList<User> users = new ArrayList<>();
        for (int i = 0; i < 2000; i++) {
            String name = "name" + i;
            User user = userMethods.getUserProfile(name);
            users.add(user);
        }

        //Give enough balance to all users
        for (User user : users) {
            userMethods.addCurrency(user.getUsername(), 1000);
        }

        //Choose a random user and make him buy a ticket
        Random rand = new Random();
        for (int i = 0; i < 500; i++) {
            int randomIndex = rand.nextInt(users.size());
            User randomUser = users.get(randomIndex);

            //Choose a random lottery, then buy a ticket
            int randomLottery = rand.nextInt(2);
            if (randomLottery == 0) {
                executorService.submit(() -> lotteryMethods.purchaseClassicTicket(randomUser.getUsername(), 1));
            } else {
                List<Integer> numbers = new ArrayList<>();
                numbers.add(1);
                numbers.add(4);
                numbers.add(15);
                numbers.add(20);
                numbers.add(35);
                numbers.add(47);
                executorService.submit(() -> lotteryMethods.purchaseLotto649Ticket(randomUser.getUsername(), numbers));
            }
        }

        start = System.nanoTime();
        executorService.shutdown();
        while (!executorService.isTerminated()) {}
        end = System.nanoTime();
        elapsed = (double) (end - start) / 1000000;
        averageElapsed = (double) (end - start) / 500000000 * threads;
        loggingService.log(LoggingService.ACTION.TOTAL_TIME, elapsed);
        loggingService.log(LoggingService.ACTION.AVERAGE_TIME, averageElapsed);
        loggingService.logStage("Stage 2 complete", true);


        executorService = Executors.newFixedThreadPool(threads);
        for (int i = 0; i < 100; i++) {
            int lottery = rand.nextInt(2);
            if (lottery == 0) {
                executorService.submit(lotteryMethods::drawClassicLottery);
            } else {
                executorService.submit(lotteryMethods::drawLotto649);
            }

        }

        start = System.nanoTime();
        executorService.shutdown();
        while (!executorService.isTerminated()) {}
        end = System.nanoTime();
        elapsed = (double) (end - start) / 1000000;
        averageElapsed = (double) (end - start) / 100000000 * threads;
        loggingService.log(LoggingService.ACTION.TOTAL_TIME, elapsed);
        loggingService.log(LoggingService.ACTION.AVERAGE_TIME, averageElapsed);
        loggingService.logStage("Stage 3 complete", false);
    }


    public static void main(String[] args) {
        new PerformanceClient().startClient();
    }
}
