package com.unlucky.unlucky.server.games.service;

import com.unlucky.unlucky.server.games.model.*;
import com.unlucky.unlucky.server.games.repository.ClassicLotteryRepository;
import com.unlucky.unlucky.server.games.repository.Lotto649Repository;
import com.unlucky.unlucky.server.user.User;
import com.unlucky.unlucky.server.user.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class LotteryService {
    private final UserRepository userRepository;
    private final ClassicLotteryRepository classicLotteryRepository;
    private final Lotto649Repository lotto649Repository;
    private final Random random = new Random();

    public LotteryService(UserRepository userRepository,
                          ClassicLotteryRepository classicLotteryRepository,
                          Lotto649Repository lotto649Repository) {
        this.userRepository = userRepository;
        this.classicLotteryRepository = classicLotteryRepository;
        this.lotto649Repository = lotto649Repository;
    }

    @Transactional
    public List<ClassicLotteryTicket> purchaseClassicTicket(String username, int quantity) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        int cost = quantity * 2; // $2 per ticket
        if (user.getBalance() < cost) {
            throw new RuntimeException("Insufficient balance");
        }

        user.setBalance(user.getBalance() - cost);
        userRepository.save(user);

        List<ClassicLotteryTicket> tickets = new ArrayList<>();
        for (int i = 0; i < quantity; i++) {
            ClassicLotteryTicket ticket = new ClassicLotteryTicket();
            ticket.setUser(user);
            ticket.setTicketNumber(generateTicketNumber());
            ticket.setPurchaseDate(LocalDateTime.now());
            ticket.setWinner(false);
            ticket.setTest(false);
            tickets.add(classicLotteryRepository.save(ticket));
        }

        return tickets;
    }

    @Transactional
    public Map<String, Object> drawClassicLottery() {
        List<ClassicLotteryTicket> allTickets = classicLotteryRepository.findAll();
        List<ClassicLotteryTicket> filteredTickets = new ArrayList<>();
        for (ClassicLotteryTicket ticket : allTickets) {
            if (ticket.isActive()) {
                filteredTickets.add(ticket);
            }
        }
        if (allTickets.isEmpty()) {
            throw new RuntimeException("No tickets to draw");
        }

        ClassicLotteryTicket winner = filteredTickets.get(random.nextInt(filteredTickets.size()));
        winner.setWinner(true);
        classicLotteryRepository.save(winner);

        User winningUser = winner.getUser();
        int prize = (int) (filteredTickets.size() * 1.5);
        winningUser.setBalance(winningUser.getBalance() + prize);
        userRepository.save(winningUser);

        for (ClassicLotteryTicket ticket : filteredTickets) {
            if(!ticket.isTest()) {
                ticket.setActive(false);
                classicLotteryRepository.save(ticket);
            }
        }

        Map<String, Object> result = new HashMap<>();
        result.put("winner", winningUser.getUsername());
        result.put("ticketNumber", winner.getTicketNumber());
        result.put("prize", prize);
        result.put("totalTickets", filteredTickets.size());

        return result;
    }

    public List<ClassicLotteryTicket> getUserClassicTickets(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return classicLotteryRepository.findByUserId(user.getId());
    }

    @Transactional
    public Lotto649Ticket purchaseLotto649Ticket(String username, List<Integer> numbers) {
        if (numbers.size() != 6 || new HashSet<>(numbers).size() != 6) {
            throw new RuntimeException("Must provide 6 unique numbers");
        }
        for (Integer number : numbers) {
            if (number < 1 || number > 49) {
                throw new RuntimeException("Numbers must be between 1 and 49");
            }
        }

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        int cost = 5; // $5 per ticket
        if (user.getBalance() < cost) {
            throw new RuntimeException("Insufficient balance");
        }

        user.setBalance(user.getBalance() - cost);
        userRepository.save(user);

        Lotto649Ticket ticket = new Lotto649Ticket();
        ticket.setUser(user);
        ticket.setNumbers(numbers);
        ticket.setPurchaseDate(LocalDateTime.now());
        ticket.setMatchingNumbers(0);
        ticket.setWinner(false);

        return lotto649Repository.save(ticket);
    }

    @Transactional
    public Map<String, Object> drawLotto649() {
        List<Integer> winningNumbers = generateWinningNumbers();
        List<Lotto649Ticket> allTickets = lotto649Repository.findAll();

        List<Lotto649Ticket> filteredTickets = new ArrayList<>();
        for (Lotto649Ticket ticket : allTickets) {
            if (ticket.isActive()) {
                filteredTickets.add(ticket);
            }
        }

        Map<String, Object> roundResult = new HashMap<>();
        roundResult.put("winningNumbers", winningNumbers);
        List<Map<String, Object>> winners = new ArrayList<>();

        for (Lotto649Ticket ticket : filteredTickets) {
            int matches = (int) ticket.getNumbers().stream()
                    .filter(winningNumbers::contains)
                    .count();

            ticket.setMatchingNumbers(matches);
            ticket.setWinner(matches >= 4);

            if (matches >= 4) {
                int prize = switch (matches) {
                    case 4 -> 100;
                    case 5 -> 1000;
                    case 6 -> 100000;
                    default -> 0;
                };
                User user = ticket.getUser();
                user.setBalance(user.getBalance() + prize);
                userRepository.save(user);

                Map<String, Object> winnerInfo = new HashMap<>();
                winnerInfo.put("username", user.getUsername());
                winnerInfo.put("matches", matches);
                winnerInfo.put("prize", prize);
                winnerInfo.put("numbers", ticket.getNumbers());
                winners.add(winnerInfo);
            }
        }

        roundResult.put("winners", winners);

        for(Lotto649Ticket ticket : filteredTickets) {
            if(!ticket.isTest()) {
                ticket.setActive(false);
            }
            lotto649Repository.save(ticket);
        }

        return roundResult;
    }

    public List<Lotto649Ticket> getUserLotto649Tickets(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return lotto649Repository.findByUserId(user.getId());
    }

    public Map<String, Object> getCurrentLotto649Round() {
        Map<String, Object> roundInfo = new HashMap<>();
        roundInfo.put("ticketsSold", lotto649Repository.count());
        roundInfo.put("lastDraw", LocalDateTime.now());
        return roundInfo;
    }

    @Transactional
    public int claimWinnings(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<Lotto649Ticket> winningTickets = lotto649Repository.findByUserId(user.getId())
                .stream().filter(Lotto649Ticket::isWinner).collect(Collectors.toList());

        if (winningTickets.isEmpty()) {
            return 0;
        }

        int totalPrize = 0;
        for (Lotto649Ticket ticket : winningTickets) {
            int prize = switch (ticket.getMatchingNumbers()) {
                case 4 -> 100;
                case 5 -> 1000;
                case 6 -> 100000;
                default -> 0;
            };
            totalPrize += prize;
            ticket.setWinner(false);
        }

        if (totalPrize > 0) {
            user.setBalance(user.getBalance() + totalPrize);
            userRepository.save(user);
        }

        lotto649Repository.saveAll(winningTickets);
        return totalPrize;
    }

    public List<Lotto649Ticket> getUserLotto649History(String username) {
        return getUserLotto649Tickets(username);
    }

    @Transactional
    public void startNewLotto649Round() {
        lotto649Repository.deleteAll();
    }

    private String generateTicketNumber() {
        return String.format("%08d", random.nextInt(100000000));
    }

    private List<Integer> generateWinningNumbers() {
        Set<Integer> numbers = new HashSet<>();
        while (numbers.size() < 6) {
            numbers.add(random.nextInt(49) + 1);
        }
        return numbers.stream().sorted().collect(Collectors.toList());
    }
}