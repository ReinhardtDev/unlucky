package com.unlucky.unlucky.server.games.model;

import com.unlucky.unlucky.server.games.repository.ClassicLotteryRepository;
import com.unlucky.unlucky.server.games.repository.Lotto649Repository;
import com.unlucky.unlucky.server.user.User;
import com.unlucky.unlucky.server.user.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Configuration
public class DatabaseInitializer {

    @Bean
    CommandLineRunner initClassicDatabase(ClassicLotteryRepository classicLotteryRepository, Lotto649Repository lotto649Repository, UserRepository userRepository) {
        return args -> {
            if (userRepository.count() == 0) {
                User user = new User("test", "test@test.com", 100000);
                userRepository.save(user);
            }

            if (classicLotteryRepository.count() == 0) {
                classicLotteryRepository.save(new ClassicLotteryTicket(userRepository.getReferenceById(1),
                                                        "#00000001", LocalDateTime.now(),
                                                        false, true, true));
            }

            if (lotto649Repository.count() == 0) {
                ArrayList<Integer> numbers = new ArrayList<>();
                numbers.add(1);
                numbers.add(12);
                numbers.add(16);
                numbers.add(28);
                numbers.add(39);
                numbers.add(45);
                lotto649Repository.save(new Lotto649Ticket(userRepository.getReferenceById(1),
                        numbers,
                        LocalDateTime.now(),
                        0, false, true, true));
            }
        };
    }
}
