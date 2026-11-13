package com.example.animecatalog.repository;

import com.example.animecatalog.entity.*;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;
/**
 * Author: cyqatester
 */
public interface UserRepository extends JpaRepository<UserAccount, Long> {
    Optional<UserAccount> findByUsername(String username);

    boolean existsByUsername(String username);

    List<UserAccount> findByNameContainingIgnoreCaseOrUsernameContainingIgnoreCase(String name, String username);
}
