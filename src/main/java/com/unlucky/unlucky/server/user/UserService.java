package com.unlucky.unlucky.server.user;

import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User createUser(String username, String email) {
        User user = new User(username, email, 0);
        return userRepository.save(user);
    }

    public Optional<String> getUserByUsername(String username) {
        return userRepository.returnUsername(username);
    }

    public Optional<User> getProfileByName(String username) {
        return userRepository.findByUsername(username);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Transactional
    public String addCurrency(){
        return "currency added";
    }
}
