package com.example.animecatalog.controller;

import com.example.animecatalog.entity.UserAccount;
import com.example.animecatalog.repository.UserRepository;
import com.example.animecatalog.service.PasswordService;
import com.example.animecatalog.service.TokenService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
/**
 * Author: cyqatester
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final UserRepository users;
    private final PasswordService passwords;
    private final TokenService tokens;

    public AuthController(UserRepository users, PasswordService passwords, TokenService tokens) {
        this.users = users;
        this.passwords = passwords;
        this.tokens = tokens;
    }

    public record LoginRequest(@NotBlank String username, @NotBlank String password) {
    }

    public record UserInfo(String username, String name, String role) {
    }

    public record LoginResponse(String token, UserInfo user) {
    }

    @PostMapping("/login")
    public LoginResponse login(@RequestBody @Valid LoginRequest request) {
        UserAccount user = users.findByUsername(request.username())
                .filter(u -> passwords.matches(request.password(), u.getPasswordHash()))
                .orElseThrow(
                        () -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid username or password"));
        return new LoginResponse(tokens.issue(user),
                new UserInfo(user.getUsername(), user.getName(), user.getRole().name()));
    }
}
