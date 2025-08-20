package com.unlucky.unlucky.server.user;

import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer>{
    @Query("SELECT u.username FROM User u WHERE u.username = :username")
    Optional<String> returnUsername(@Param("username") String username);

    Optional<User> findByUsername(String username);
}
