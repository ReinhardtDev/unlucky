package com.unlucky.unlucky.server.user;

import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public User register(@RequestBody User user) {
        return userService.createUser(user.getUsername(), user.getEmail());
    }

    @GetMapping("/{username}/username")
    public Optional<String> getUser(@PathVariable String username) {
        return userService.getUserByUsername(username);
    }

    @GetMapping("/{username}/profile")
    public Optional<User> getProfile(@PathVariable String username) {
        return userService.getProfileByName(username);
    }

    @PostMapping("/{username}/add-currency")
    public User addCurrency(@PathVariable String username, @RequestParam int amount) {
        return userService.addCurrency(username, amount);
    }
}
