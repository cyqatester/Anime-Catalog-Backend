package com.example.animecatalog.service;

import com.example.animecatalog.entity.*;
import com.example.animecatalog.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;
/**
 * Author: cyqatester
 */
@Service
public class ActorService {
    private final UserRepository users;

    public ActorService(UserRepository users) {
        this.users = users;
    }

    public UserAccount actor(String username) {
        if (username == null || username.isBlank())
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Supply X-Username header");
        return users.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unknown user"));
    }

    public UserAccount admin(String username) {
        UserAccount u = actor(username);
        if (u.getRole() != UserRole.ADMIN)
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Admin role required");
        return u;
    }
}
