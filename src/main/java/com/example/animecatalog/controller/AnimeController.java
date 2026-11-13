package com.example.animecatalog.controller;

import com.example.animecatalog.entity.*;
import com.example.animecatalog.repository.*;
import com.example.animecatalog.service.ActorService;
import jakarta.validation.Valid;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.*;
/**
 * Author: cyqatester
 */
@RestController
@RequestMapping("/api/anime")
public class AnimeController {
    private final AnimeRepository anime;
    private final GenreRepository genres;
    private final ActorService actors;

    public AnimeController(AnimeRepository a, GenreRepository g, ActorService s) {
        anime = a;
        genres = g;
        actors = s;
    }

    public record AnimeInput(String title, String alternateTitle, Set<Long> genreIds, Integer yearAired,
            ReleaseCategory releaseCategory, Integer episodes, String season) {
    }

    @GetMapping
    public List<Anime> search(
            Authentication authentication,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String genre,
            @RequestParam(required = false) Integer yearAired,
            @RequestParam(required = false) ReleaseCategory releaseCategory) {

        actors.actor(authentication.getName());

        String normalizedTitle =
                title == null ? "" : title.trim().toLowerCase(Locale.ROOT);

        String normalizedGenre =
                genre == null ? "" : genre.trim().toLowerCase(Locale.ROOT);

        return anime.search(
                normalizedTitle,
                normalizedGenre,
                yearAired,
                releaseCategory
        );
    }

    @PostMapping
    public Anime create(
        Authentication authentication, 
        @RequestBody @Valid AnimeInput in) {
        actors.admin(authentication.getName());
        Anime a = new Anime();
        apply(in, a);
        return anime.save(a);
    }

    @PutMapping("/{id}")
    public Anime update(
            Authentication authentication, 
            @PathVariable Long id,
            @RequestBody @Valid AnimeInput in) {
        actors.admin(authentication.getName());
        if (!anime.existsById(id))
            throw new org.springframework.web.server.ResponseStatusException(
                    org.springframework.http.HttpStatus.NOT_FOUND);
        Anime existing = anime.findById(id)
                .orElseThrow(() -> new org.springframework.web.server.ResponseStatusException(
                        org.springframework.http.HttpStatus.NOT_FOUND));
        apply(in, existing);
        return anime.save(existing);
    }

    @DeleteMapping("/{id}")
    public void delete(
            Authentication authentication,  
            @PathVariable Long id) {
        actors.admin(authentication.getName());
        anime.deleteById(id);
    }

    private void apply(AnimeInput i, Anime a) {
        if (i.title() == null || i.title().isBlank() || i.alternateTitle() == null || i.alternateTitle().isBlank()
                || i.yearAired() == null || i.releaseCategory() == null || i.genreIds() == null
                || i.genreIds().isEmpty())
            throw new org.springframework.web.server.ResponseStatusException(
                    org.springframework.http.HttpStatus.BAD_REQUEST,
                    "Title, alternateTitle, genreIds, yearAired and releaseCategory are required");
        a.setTitle(i.title());
        a.setAlternateTitle(i.alternateTitle());
        a.setYearAired(i.yearAired());
        a.setReleaseCategory(i.releaseCategory());
        a.setEpisodes(i.episodes());
        a.setSeason(i.season());
        Set<Genre> gs = new HashSet<>(genres.findAllById(i.genreIds()));
        if (gs.size() != i.genreIds().size())
            throw new org.springframework.web.server.ResponseStatusException(
                    org.springframework.http.HttpStatus.BAD_REQUEST, "Unknown genre id");
        a.setGenres(gs);
    }
}
