package com.example.animecatalog.repository;

import com.example.animecatalog.entity.Genre;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;
/**
 * Author: cyqatester
 */
public interface GenreRepository extends JpaRepository<Genre, Long> {
    Optional<Genre> findByNameIgnoreCase(String name);

    List<Genre> findByNameContainingIgnoreCase(String name);
}
