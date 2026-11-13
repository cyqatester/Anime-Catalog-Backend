package com.example.animecatalog.repository;

import com.example.animecatalog.entity.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import java.util.*;
/**
 * Author: cyqatester
 */
public interface AnimeRepository extends JpaRepository<Anime, Long> {
    @Query("select distinct a from Anime a join a.genres g where (:title = '' or lower(a.title) like concat('%', :title, '%') or lower(a.alternateTitle) like concat('%', :title, '%')) and (:genre = '' or lower(g.name) = :genre) and (:year is null or a.yearAired = :year) and (:category is null or a.releaseCategory = :category)")
    List<Anime> search(
            @Param("title") String title, 
            @Param("genre") String genre, 
            @Param("year") Integer year,
            @Param("category") ReleaseCategory category);
}
