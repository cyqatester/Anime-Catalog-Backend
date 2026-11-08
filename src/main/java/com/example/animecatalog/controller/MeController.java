package com.example.animecatalog.controller;

import com.example.animecatalog.entity.*;
import com.example.animecatalog.repository.UserRepository;
import com.example.animecatalog.service.*;
import jakarta.validation.constraints.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/me")
public class MeController {
    private final ActorService actors;
    private final UserRepository users;
    private final PasswordService passwords;

    public MeController(ActorService a, UserRepository u, PasswordService p) {
        actors = a;
        users = u;
        passwords = p;
    }

    public record Profile(String name, String username, UserRole role) {
    }

    public record PasswordChange(@NotBlank String newPassword) {
    }

    public record UsernameChange(@NotBlank String username) {
    }

    @GetMapping
    public Profile me(@RequestHeader("X-Username") String username) {
        var u = actors.actor(username);
        return new Profile(u.getName(), u.getUsername(), u.getRole());
    }

    @PutMapping("/password")
    public void password(@RequestHeader("X-Username") String username,
            @RequestBody @jakarta.validation.Valid PasswordChange body) {
        var u = actors.actor(username);
        u.setPasswordHash(passwords.hash(body.newPassword()));
        users.save(u);
    }

    @PutMapping("/username")
    public Profile username(@RequestHeader("X-Username") String username,
            @RequestBody @jakarta.validation.Valid UsernameChange body) {
        var u = actors.admin(username);
        u.setUsername(body.username());
        users.save(u);
        return new Profile(u.getName(), u.getUsername(), u.getRole());
    }
}
