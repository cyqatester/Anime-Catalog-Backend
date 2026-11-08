package com.example.animecatalog.controller;

import com.example.animecatalog.entity.Genre;
import com.example.animecatalog.repository.GenreRepository;
import com.example.animecatalog.service.ActorService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/api/genres")
public class GenreController {
    private final GenreRepository repo;
    private final ActorService actors;

    public GenreController(GenreRepository r, ActorService a) {
        repo = r;
        actors = a;
    }

    public record GenreInput(@NotBlank String name) {
    }

    @GetMapping
    public List<Genre> list(@RequestHeader("X-Username") String username,
            @RequestParam(required = false) String search) {
        actors.actor(username);
        return search == null ? repo.findAll() : repo.findByNameContainingIgnoreCase(search);
    }

    @PostMapping
    public Genre create(@RequestHeader("X-Username") String username, @RequestBody @Valid GenreInput in) {
        actors.admin(username);
        return repo.save(new Genre(in.name()));
    }

    @PutMapping("/{id}")
    public Genre update(@RequestHeader("X-Username") String username, @PathVariable Long id,
            @RequestBody @Valid GenreInput in) {
        actors.admin(username);
        Genre g = repo.findById(id).orElseThrow(() -> new org.springframework.web.server.ResponseStatusException(
                org.springframework.http.HttpStatus.NOT_FOUND));
        g.setName(in.name());
        return repo.save(g);
    }

    @DeleteMapping("/{id}")
    public void delete(@RequestHeader("X-Username") String username, @PathVariable Long id) {
        actors.admin(username);
        repo.deleteById(id);
    }
}
