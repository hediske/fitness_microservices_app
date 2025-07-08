package com.fitness.hediske.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fitness.hediske.entities.User;

public interface  UserRepository extends JpaRepository<User, Long> {
    
    // Custom query methods can be defined here if needed
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);

    
}
