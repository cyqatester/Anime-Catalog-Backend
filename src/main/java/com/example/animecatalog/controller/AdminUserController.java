package com.example.animecatalog.controller;

import com.example.animecatalog.entity.*;
import com.example.animecatalog.repository.UserRepository;
import com.example.animecatalog.service.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/api/admin/users")
public class AdminUserController {
    private final UserRepository users;
    private final ActorService actors;
    private final PasswordService passwords;

    public AdminUserController(UserRepository u, ActorService a, PasswordService p) {
        users = u;
        actors = a;
        passwords = p;
    }

    public record UserInput(@NotBlank String name, @NotBlank String username, @NotBlank String password,
            @NotNull UserRole role) {
    }

    public record UserView(Long id, String name, String username, UserRole role, boolean superAdmin) {
    }

    @GetMapping
    public List<UserView> list(@RequestHeader("X-Username") String username,
            @RequestParam(required = false) String search) {
        actors.admin(username);
        return (search == null ? users.findAll()
                : users.findByNameContainingIgnoreCaseOrUsernameContainingIgnoreCase(search, search)).stream()
                .map(this::view).toList();
    }

    @PostMapping
    public UserView create(@RequestHeader("X-Username") String username, @RequestBody @Valid UserInput in) {
        actors.admin(username);
        return view(
                users.save(new UserAccount(in.name(), in.username(), passwords.hash(in.password()), in.role(), false)));
    }

    @PutMapping("/{id}")
    public UserView update(@RequestHeader("X-Username") String username, @PathVariable Long id,
            @RequestBody @Valid UserInput in) {
        actors.admin(username);
        UserAccount u = users.findById(id).orElseThrow(() -> new org.springframework.web.server.ResponseStatusException(
                org.springframework.http.HttpStatus.NOT_FOUND));
        u.setName(in.name());
        u.setUsername(in.username());
        u.setPasswordHash(passwords.hash(in.password()));
        u.setRole(in.role());
        return view(users.save(u));
    }

    @DeleteMapping("/{id}")
    public void delete(@RequestHeader("X-Username") String username, @PathVariable Long id) {
        UserAccount actor = actors.admin(username);
        UserAccount target = users.findById(id)
                .orElseThrow(() -> new org.springframework.web.server.ResponseStatusException(
                        org.springframework.http.HttpStatus.NOT_FOUND));
        if (target.isSuperAdmin() || target.getId().equals(actor.getId()))
            throw new org.springframework.web.server.ResponseStatusException(
                    org.springframework.http.HttpStatus.FORBIDDEN, "Cannot delete superadmin or yourself");
        users.delete(target);
    }

    private UserView view(UserAccount u) {
        return new UserView(u.getId(), u.getName(), u.getUsername(), u.getRole(), u.isSuperAdmin());
    }
}
