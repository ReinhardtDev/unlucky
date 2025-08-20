package com.unlucky.unlucky.server.user;

import jakarta.persistence.*;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;
    private String email;
    private int balance;

    public User() {}

    public User(String username, String email, int balance) {
        this.username = username;
        this.email = email;
        this.balance = balance;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public int getBalance() { return balance; }
    public void setBalance(int balance) { this.balance = balance; }

}
