package com.unlucky.unlucky.client;

import com.unlucky.unlucky.logging.LoggingService;
import com.unlucky.unlucky.server.user.UserService;
import com.unlucky.unlucky.server.user.User;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import java.util.Optional;

public class UserMethods {

    private UserService userService;
    private LoggingService loggingService;
    private static ApplicationContext context;

    public UserMethods() {
        initializeServices();
    }

    private synchronized void initializeServices() {
        if (context == null) {
            try {
                context = new AnnotationConfigApplicationContext("com.unlucky.unlucky");
            } catch (Exception e) {
                System.err.println("Failed to initialize Spring context: " + e.getMessage());
                return;
            }
        }

        try {
            this.userService = context.getBean(UserService.class);
            this.loggingService = context.getBean(LoggingService.class);
        } catch (Exception e) {
            System.err.println("Failed to get UserService: " + e.getMessage());
        }
    }

    public String returnUserByName(String username) {
        if (userService == null) {
            initializeServices();
            if (userService == null) {
                return username; // Fallback
            }
        }

        try {
            return userService.getUserByUsername(username).orElse(null);
        } catch (Exception e) {
            System.err.println("Error getting user by name: " + e.getMessage());
            return null;
        }
    }

    public void registerUser(String username, String email) {
        if (userService == null) {
            initializeServices();
            if (userService == null) {
                throw new RuntimeException("UserService not available. Cannot register user.");
            }
        }

        try {
            long start = System.nanoTime();

            userService.createUser(username, email);

            long end = System.nanoTime();
            double elapsed = (double) (end - start) / 1000000;
            loggingService.log(LoggingService.ACTION.REGISTER_USER, elapsed);
        } catch (Exception e) {
            throw new RuntimeException("Failed to register user: " + e.getMessage());
        }
    }

    public boolean isValidEmail(String email) {
        return email != null && email.matches("^[A-Za-z0-9+_.-]+@(.+)$");
    }

    public void displayUserProfile(String username) {
        if (userService == null) {
            initializeServices();
            if (userService == null) {
                System.out.println("UserService not available. Cannot display profile.");
                return;
            }
        }

        try {
            Optional<User> user = userService.getProfileByName(username);
            if (user.isPresent()) {
                User u = user.get();
                System.out.println("Username: " + u.getUsername() +
                        ", Email: " + u.getEmail() +
                        ", Balance: " + u.getBalance());
            } else {
                System.out.println("User not found");
            }
        } catch (Exception e) {
            System.out.println("Error displaying profile: " + e.getMessage());
        }
    }

    public void addCurrency(String username, int amount) {
        if (userService == null) {
            initializeServices();
            if (userService == null) {
                throw new RuntimeException("UserService not available. Cannot add currency.");
            }
        }

        try {
            userService.addCurrency(username, amount);
        } catch (Exception e) {
            throw new RuntimeException("Failed to add currency: " + e.getMessage());
        }
    }

    public int getBalance(String username) {
        if (userService == null) {
            initializeServices();
            if (userService == null) {
                return 100; // Fallback balance
            }
        }

        try {
            Optional<User> user = userService.getProfileByName(username);
            return user.map(User::getBalance).orElse(0);
        } catch (Exception e) {
            System.err.println("Error getting balance: " + e.getMessage());
            return 0;
        }
    }

    public static ApplicationContext getContext() {
        if (context == null) {
            try {
                context = new AnnotationConfigApplicationContext("com.unlucky.unlucky");
            } catch (Exception e) {
                System.err.println("Failed to initialize Spring context: " + e.getMessage());
            }
        }
        return context;
    }
}