package com.unlucky.unlucky.server.games.model;

import com.unlucky.unlucky.server.user.User;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "lotto649_tickets")
public class Lotto649Ticket {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ElementCollection
    @CollectionTable(name = "lotto649_numbers", joinColumns = @JoinColumn(name = "ticket_id"))
    @Column(name = "number")
    private List<Integer> numbers;

    private LocalDateTime purchaseDate;
    private Integer matchingNumbers;
    private boolean winner;

    public Lotto649Ticket() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public List<Integer> getNumbers() { return numbers; }
    public void setNumbers(List<Integer> numbers) { this.numbers = numbers; }

    public LocalDateTime getPurchaseDate() { return purchaseDate; }
    public void setPurchaseDate(LocalDateTime purchaseDate) { this.purchaseDate = purchaseDate; }

    public Integer getMatchingNumbers() { return matchingNumbers; }
    public void setMatchingNumbers(Integer matchingNumbers) { this.matchingNumbers = matchingNumbers; }

    public boolean isWinner() { return winner; }
    public void setWinner(boolean winner) { this.winner = winner; }
}
