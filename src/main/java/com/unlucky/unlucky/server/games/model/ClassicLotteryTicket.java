package com.unlucky.unlucky.server.games.model;

import com.unlucky.unlucky.server.user.User;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "classic_lottery_tickets")
public class ClassicLotteryTicket {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    private String ticketNumber;
    private LocalDateTime purchaseDate;
    private boolean winner;
    private boolean active;
    private boolean test;

    public ClassicLotteryTicket() {}

    public ClassicLotteryTicket(User user, String ticketNumber, LocalDateTime purchaseDate, boolean winner, boolean active, boolean test) {
        this.user = user;
        this.ticketNumber = ticketNumber;
        this.purchaseDate = purchaseDate;
        this.winner = winner;
        this.active = active;
        this.test = test;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public String getTicketNumber() { return ticketNumber; }
    public void setTicketNumber(String ticketNumber) { this.ticketNumber = ticketNumber; }

    public LocalDateTime getPurchaseDate() { return purchaseDate; }
    public void setPurchaseDate(LocalDateTime purchaseDate) { this.purchaseDate = purchaseDate; }

    public boolean isWinner() { return winner; }
    public void setWinner(boolean winner) { this.winner = winner; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }

    public boolean isTest() { return test; }
    public void setTest(boolean test) { this.test = test; }
}