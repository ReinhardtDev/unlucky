package com.unlucky.unlucky.server.games.controller;

import com.unlucky.unlucky.server.games.model.*;
import com.unlucky.unlucky.server.games.service.LotteryService;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/lottery")
public class LotteryController {

    private final LotteryService lotteryService;

    public LotteryController(LotteryService lotteryService) {
        this.lotteryService = lotteryService;
    }

    @PostMapping("/classic/purchase")
    public List<ClassicLotteryTicket> purchaseClassicTicket(
            @RequestParam String username,
            @RequestBody Map<String, Integer> requestBody) {
        int quantity = requestBody.get("quantity");
        return lotteryService.purchaseClassicTicket(username, quantity);
    }

    @PostMapping("/classic/draw")
    public Map<String, Object> drawClassicLottery() {
        return lotteryService.drawClassicLottery();
    }

    @GetMapping("/classic/tickets")
    public List<ClassicLotteryTicket> getClassicTickets(@RequestParam String username) {
        return lotteryService.getUserClassicTickets(username);
    }

    @GetMapping("/classic/tickets/history")
    public List<ClassicLotteryTicket> getClassicTicketHistory(@RequestParam String username) {
        return lotteryService.getUserClassicTicketHistory(username);
    }

    @PostMapping("/lotto649/purchase")
    public Lotto649Ticket purchaseLotto649Ticket(
            @RequestParam String username,
            @RequestBody List<Integer> numbers) {
        return lotteryService.purchaseLotto649Ticket(username, numbers);
    }

    @PostMapping("/lotto649/draw")
    public Map<String, Object> drawLotto649() {
        return lotteryService.drawLotto649();
    }

    @GetMapping("/lotto649/tickets")
    public List<Lotto649Ticket> getLotto649Tickets(@RequestParam String username) {
        return lotteryService.getUserLotto649Tickets(username);
    }

    @GetMapping("/lotto649/tickets/history")
    public List<Lotto649Ticket> getLotto649TicketHistory(@RequestParam String username) {
        return lotteryService.getUserLotto649TicketHistory(username);
    }

    @PostMapping("/lotto649/claim")
    public int claimWinnings(@RequestParam String username) {
        return lotteryService.claimWinnings(username);
    }

    @PostMapping("/lotto649/new-round")
    public void startNewRound() {
        lotteryService.startNewLotto649Round();
    }

    @GetMapping("/lotto649/round-info")
    public Map<String, Object> getRoundInfo() {
        return lotteryService.getCurrentLotto649Round();
    }
}