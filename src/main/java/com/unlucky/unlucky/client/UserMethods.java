package com.unlucky.unlucky.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.unlucky.unlucky.logging.LoggingService;
import com.unlucky.unlucky.server.user.User;

import java.io.IOException;

public class UserMethods {

    private final LoggingService loggingService = new LoggingService();
    private final Connection connection = new Connection("http://localhost:8080");
    private final ObjectMapper mapper = new ObjectMapper();

    public UserMethods() {}

    public String returnUserByName(String username) throws JsonProcessingException {
        return mapper.readValue(connection.restGetRequest("/api/users/" +  username + "/username"), String.class);
    }

    public void registerUser(String username, String email){
        long start = System.nanoTime();
        String json = String.format("{\"username\":\"%s\", \"email\":\"%s\"}", username, email);
        connection.restPostRequest(json, "/api/users/register");
        long end = System.nanoTime();
        double elapsed = (double) (end - start) / 1000000;
        loggingService.log(LoggingService.ACTION.REGISTER_USER, elapsed);
    }

    public void displayUserProfile(String username) throws JsonProcessingException {
        User user = mapper.readValue(connection.restGetRequest("/api/users/" + username + "/profile"), User.class);
        System.out.println("Username: " + user.getUsername());
        System.out.println("Email: " + user.getEmail());
        System.out.println("Balance: " + user.getBalance());
    }

    public void addCurrency(String username, int amount) {
        connection.restPostRequest("{}", "/api/users/" + username + "/add-currency?amount=" + amount);
    }

    public boolean isValidEmail(String email) {
        if(email == null) return false;
        String regex = "^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$";
        return email.matches(regex);
    }

    public void testTCPConnection() throws IOException {
        connection.startConnection("localhost", 5000);
        String response = connection.sendMessage("help");
        System.out.println("Server response: " + response);

        connection.stopConnection();
    }
}
