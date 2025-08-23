package com.unlucky.unlucky.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.unlucky.unlucky.server.user.User;

public class UserMethods {

    private final Connection connection = new Connection("http://localhost:8080");
    private final ObjectMapper mapper = new ObjectMapper();

    public UserMethods() {

    }

    public String returnUserByName(String username) throws JsonProcessingException {
        return mapper.readValue(connection.sendGetRequest("/api/users/" +  username + "/username"), String.class);
    }

    public void registerUser(String username, String email){
        String json = String.format("{\"username\":\"%s\", \"email\":\"%s\"}", username, email);
        connection.sendPostRequest(json, "/api/users/register");
    }

    public void displayUserProfile(String username) throws JsonProcessingException {
        User user = mapper.readValue(connection.sendGetRequest("/api/users/" + username + "/profile"), User.class);
        System.out.println("Username: " + user.getUsername());
        System.out.println("Email: " + user.getEmail());
        System.out.println("Balance: " + user.getBalance());
    }

    public void addCurrency(String username, int amount) {
        connection.sendPostRequest("{}", "/api/users/" + username + "/add-currency?amount=" + amount);
    }

    public boolean isValidEmail(String email) {
        if(email == null) return false;
        String regex = "^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$";
        return email.matches(regex);
    }
}
