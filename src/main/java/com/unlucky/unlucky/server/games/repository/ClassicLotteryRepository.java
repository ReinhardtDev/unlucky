package com.unlucky.unlucky.server.games.repository;

import com.unlucky.unlucky.server.games.model.ClassicLotteryTicket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ClassicLotteryRepository extends JpaRepository<ClassicLotteryTicket, Long> {
    List<ClassicLotteryTicket> findByUserId(Long userId);
    List<ClassicLotteryTicket> findByWinnerTrue();
}