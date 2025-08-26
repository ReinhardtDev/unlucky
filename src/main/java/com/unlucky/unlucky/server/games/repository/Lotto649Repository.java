package com.unlucky.unlucky.server.games.repository;

import com.unlucky.unlucky.server.games.model.Lotto649Ticket;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

@Repository
public interface Lotto649Repository extends JpaRepository<Lotto649Ticket, Long> {
    List<Lotto649Ticket> findByUserId(Long userId);
    List<Lotto649Ticket> findByWinnerTrue();
}