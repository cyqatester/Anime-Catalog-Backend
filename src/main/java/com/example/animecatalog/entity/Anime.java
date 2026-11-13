package com.example.animecatalog.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.util.*;
/**
 * Author: cyqatester
 */
@Entity
@Table(name = "anime")
public class Anime {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotBlank
    @Column(nullable = false)
    private String title;
    @NotBlank
    @Column(nullable = false)
    private String alternateTitle;
    @NotNull
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "anime_genres", joinColumns = @JoinColumn(name = "anime_id"), inverseJoinColumns = @JoinColumn(name = "genre_id"))
    private Set<Genre> genres = new HashSet<>();
    @NotNull
    @Min(1900)
    @Max(2100)
    @Column(nullable = false)
    private Integer yearAired;
    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReleaseCategory releaseCategory;
    @Min(1)
    private Integer episodes;
    private String season;

    public Anime() {
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String v) {
        title = v;
    }

    public String getAlternateTitle() {
        return alternateTitle;
    }

    public void setAlternateTitle(String v) {
        alternateTitle = v;
    }

    public Set<Genre> getGenres() {
        return genres;
    }

    public void setGenres(Set<Genre> v) {
        genres = v;
    }

    public Integer getYearAired() {
        return yearAired;
    }

    public void setYearAired(Integer v) {
        yearAired = v;
    }

    public ReleaseCategory getReleaseCategory() {
        return releaseCategory;
    }

    public void setReleaseCategory(ReleaseCategory v) {
        releaseCategory = v;
    }

    public Integer getEpisodes() {
        return episodes;
    }

    public void setEpisodes(Integer v) {
        episodes = v;
    }

    public String getSeason() {
        return season;
    }

    public void setSeason(String v) {
        season = v;
    }
}
